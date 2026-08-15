package com.roboticswala.hub.data.repository

import com.roboticswala.hub.data.models.Project
import com.roboticswala.hub.data.models.ProjectUpdate
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {

    /**
     * Creates a new project in Firestore collection "projects".
     * Optionally uploads project image to Firebase Storage.
     */
    fun createProject(project: Project, imageBytes: ByteArray? = null): Flow<Resource<Project>>

    /**
     * Updates an existing project. Only the project owner or admin can edit project parameters.
     */
    fun updateProject(project: Project, imageBytes: ByteArray? = null): Flow<Resource<Unit>>

    /**
     * Deletes a project. Only allowed by project owner and if status is not "Completed".
     */
    fun deleteProject(projectId: String, studentUid: String): Flow<Resource<Unit>>

    /**
     * Real-time listener for projects owned by or assigned to the student.
     */
    fun observeStudentProjects(studentUid: String): Flow<List<Project>>

    /**
     * Real-time listener for single project details.
     */
    fun observeProjectDetails(projectId: String): Flow<Project?>

    /**
     * Real-time listener for project updates subcollection: /projects/{projectId}/updates.
     */
    fun observeProjectUpdates(projectId: String): Flow<List<ProjectUpdate>>

    /**
     * Adds a progress update to /projects/{projectId}/updates and updates progress % + updatedAt on the project.
     */
    fun addProjectUpdate(
        projectId: String,
        update: ProjectUpdate,
        imageBytes: ByteArray? = null
    ): Flow<Resource<Unit>>

    /**
     * Real-time listener for all projects for Admin management with category & status filters.
     */
    fun observeAllAdminProjects(
        category: String? = null,
        status: String? = null
    ): Flow<List<Project>>

    /**
     * Admin action to update project status or assign/modify mentor.
     */
    fun adminUpdateProject(
        projectId: String,
        status: String? = null,
        mentorName: String? = null
    ): Flow<Resource<Unit>>

    /**
     * Fetches all approved students to select as team members.
     */
    fun fetchApprovedStudents(): Flow<List<UserProfile>>
}
