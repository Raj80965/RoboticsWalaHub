package com.roboticswala.hub.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.roboticswala.hub.data.models.Project
import com.roboticswala.hub.data.models.ProjectUpdate
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirestoreProjectRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) : ProjectRepository {

    private val projectsCollection = firestore.collection("projects")
    private val usersCollection = firestore.collection("users")

    override fun createProject(
        project: Project,
        imageBytes: ByteArray?
    ): Flow<Resource<Project>> = flow {
        emit(Resource.Loading())
        try {
            if (project.title.isBlank()) {
                emit(Resource.Error("Project title is required."))
                return@flow
            }

            val docRef = if (project.projectId.isNotBlank()) {
                projectsCollection.document(project.projectId)
            } else {
                projectsCollection.document()
            }

            var imageUrl = project.projectImageUrl
            if (imageBytes != null && imageBytes.isNotEmpty()) {
                val storageRef = storage.reference.child("projects/${docRef.id}/banner_${System.currentTimeMillis()}.jpg")
                storageRef.putBytes(imageBytes).await()
                imageUrl = storageRef.downloadUrl.await().toString()
            }

            val finalProject = project.copy(
                projectId = docRef.id,
                projectImageUrl = imageUrl,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            docRef.set(finalProject).await()
            emit(Resource.Success(finalProject))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to create project."))
        }
    }

    override fun updateProject(
        project: Project,
        imageBytes: ByteArray?
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            var imageUrl = project.projectImageUrl
            if (imageBytes != null && imageBytes.isNotEmpty()) {
                val storageRef = storage.reference.child("projects/${project.projectId}/banner_${System.currentTimeMillis()}.jpg")
                storageRef.putBytes(imageBytes).await()
                imageUrl = storageRef.downloadUrl.await().toString()
            }

            val updatedProject = project.copy(
                projectImageUrl = imageUrl,
                updatedAt = System.currentTimeMillis()
            )

            projectsCollection.document(project.projectId).set(updatedProject).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to update project."))
        }
    }

    override fun deleteProject(projectId: String, studentUid: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val doc = projectsCollection.document(projectId).get().await()
            if (!doc.exists()) {
                emit(Resource.Error("Project not found."))
                return@flow
            }

            val project = doc.toObject(Project::class.java)
            if (project == null || project.ownerUid != studentUid) {
                emit(Resource.Error("Unauthorized: Only the project owner can delete this project."))
                return@flow
            }

            if (project.isCompleted) {
                emit(Resource.Error("Completed projects cannot be deleted as they are archived in the lab registry."))
                return@flow
            }

            projectsCollection.document(projectId).delete().await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to delete project."))
        }
    }

    override fun observeStudentProjects(studentUid: String): Flow<List<Project>> = callbackFlow {
        val listener = projectsCollection
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val all = snapshot?.documents?.mapNotNull { it.toObject(Project::class.java) } ?: emptyList()
                val userProjects = all.filter { it.isUserAuthorized(studentUid) }
                trySend(userProjects)
            }

        awaitClose { listener.remove() }
    }

    override fun observeProjectDetails(projectId: String): Flow<Project?> = callbackFlow {
        val listener = projectsCollection.document(projectId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val project = snapshot.toObject(Project::class.java)
                trySend(project)
            }

        awaitClose { listener.remove() }
    }

    override fun observeProjectUpdates(projectId: String): Flow<List<ProjectUpdate>> = callbackFlow {
        val listener = projectsCollection.document(projectId)
            .collection("updates")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(ProjectUpdate::class.java) } ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    override fun addProjectUpdate(
        projectId: String,
        update: ProjectUpdate,
        imageBytes: ByteArray?
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val updatesColl = projectsCollection.document(projectId).collection("updates")
            val updateDocRef = updatesColl.document()

            var imageUrl = update.imageUrl
            if (imageBytes != null && imageBytes.isNotEmpty()) {
                val storageRef = storage.reference.child("projects/$projectId/updates/${updateDocRef.id}.jpg")
                storageRef.putBytes(imageBytes).await()
                imageUrl = storageRef.downloadUrl.await().toString()
            }

            val finalUpdate = update.copy(
                updateId = updateDocRef.id,
                projectId = projectId,
                imageUrl = imageUrl,
                progressPercentage = update.progressPercentage.coerceIn(0, 100),
                createdAt = System.currentTimeMillis()
            )

            updateDocRef.set(finalUpdate).await()

            // Synchronize Progress & Status on main Project doc
            val updateMap = mutableMapOf<String, Any>(
                "progressPercentage" to finalUpdate.progressPercentage,
                "updatedAt" to System.currentTimeMillis()
            )
            if (finalUpdate.progressPercentage >= 100) {
                updateMap["status"] = Project.STATUS_COMPLETED
            }

            projectsCollection.document(projectId).update(updateMap).await()

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to post progress update."))
        }
    }

    override fun observeAllAdminProjects(
        category: String?,
        status: String?
    ): Flow<List<Project>> = callbackFlow {
        var query: Query = projectsCollection.orderBy("updatedAt", Query.Direction.DESCENDING)

        if (!category.isNullOrBlank() && category != "All") {
            query = query.whereEqualTo("category", category)
        }
        if (!status.isNullOrBlank() && status != "All") {
            query = query.whereEqualTo("status", status)
        }

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toObject(Project::class.java) } ?: emptyList()
            trySend(list)
        }

        awaitClose { listener.remove() }
    }

    override fun adminUpdateProject(
        projectId: String,
        status: String?,
        mentorName: String?
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val map = mutableMapOf<String, Any>("updatedAt" to System.currentTimeMillis())
            if (!status.isNullOrBlank()) map["status"] = status
            if (mentorName != null) map["mentorName"] = mentorName

            projectsCollection.document(projectId).update(map).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to update project."))
        }
    }

    override fun fetchApprovedStudents(): Flow<List<UserProfile>> = callbackFlow {
        val listener = usersCollection
            .whereEqualTo("status", "Approved")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.data?.let { UserProfile.fromMap(it) }
                } ?: emptyList()
                trySend(list)
            }

        awaitClose { listener.remove() }
    }
}
