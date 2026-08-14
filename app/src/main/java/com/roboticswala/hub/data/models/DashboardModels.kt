package com.roboticswala.hub.data.models

// Student Models
data class StudentDashboardData(
    val studentName: String = "Aarav Sharma",
    val studentId: String = "RWH-STU-2026-042",
    val roleBadge: String = "Robotics Lead • Year 3",
    val attendancePercentage: Double = 94.5,
    val totalWorkingHours: Int = 128,
    val todaySlot: LabSlot = LabSlot(
        time = "02:00 PM - 05:00 PM",
        station = "Autonomous Robotics Bay 3",
        topic = "SLAM Sensor Calibration & LiDaR Testing",
        status = "Confirmed"
    ),
    val currentProject: ProjectSummary = ProjectSummary(
        title = "Autonomous Obstacle Avoidance Rover",
        category = "UGV / Mobile Robotics",
        progress = 78,
        status = "Active Testing",
        nextMilestone = "ROS2 Navigation Stack Integration"
    ),
    val latestNotice: NoticeItem = NoticeItem(
        title = "RoboWars 2026 Registrations Open",
        description = "Submit your combat robot CAD designs before Friday 6 PM to qualify for arena testing.",
        date = "Today, 10:30 AM",
        tag = "Competition",
        isUrgent = true
    ),
    val upcomingEvent: EventItem = EventItem(
        title = "ROS2 & Autonomous Drone Bootcamp",
        venue = "Main Automation Lab (Hall B)",
        dateTime = "Aug 22 • 10:00 AM - 04:00 PM",
        speaker = "Dr. Vikram Sethi (Robotics AI)"
    )
)

data class LabSlot(
    val time: String,
    val station: String,
    val topic: String,
    val status: String
)

data class ProjectSummary(
    val title: String,
    val category: String,
    val progress: Int,
    val status: String,
    val nextMilestone: String
)

data class ProjectItem(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val progress: Int,
    val status: String,
    val techStack: List<String>,
    val teamMembers: Int
)

data class ActivityLogItem(
    val id: String,
    val title: String,
    val location: String,
    val timestamp: String,
    val duration: String,
    val type: String
)

data class NoticeItem(
    val title: String,
    val description: String,
    val date: String,
    val tag: String,
    val isUrgent: Boolean = false
)

data class EventItem(
    val title: String,
    val venue: String,
    val dateTime: String,
    val speaker: String
)

// Admin Models
data class AdminDashboardData(
    val totalStudents: Int = 248,
    val pendingApprovals: Int = 14,
    val activeStudents: Int = 186,
    val todayAttendancePercentage: Double = 92.4,
    val activeProjects: Int = 36,
    val pendingBookings: Int = 8,
    val lowStockEquipment: List<EquipmentItem> = listOf(
        EquipmentItem("LiPo 4S 5200mAh Batteries", "Lab Stock: 2 units remaining", "Critical", 2),
        EquipmentItem("Arduino Nano V3 (ATmega328P)", "Lab Stock: 4 units remaining", "Low", 4),
        EquipmentItem("MG996R Metal Gear Servos", "Lab Stock: 3 units remaining", "Low", 3),
        EquipmentItem("RP-LiDAR A1M8 Scanner", "Lab Stock: 1 unit remaining", "Critical", 1)
    )
)

data class EquipmentItem(
    val name: String,
    val stockDetail: String,
    val alertLevel: String,
    val quantity: Int
)

data class StudentDirectoryItem(
    val id: String,
    val name: String,
    val email: String,
    val rfidStatus: String,
    val currentProject: String,
    val attendance: Double,
    val status: String
)

data class BookingRequestItem(
    val id: String,
    val studentName: String,
    val studentId: String,
    val bayOrMachine: String,
    val timeSlot: String,
    val date: String,
    val purpose: String,
    val status: String
)
