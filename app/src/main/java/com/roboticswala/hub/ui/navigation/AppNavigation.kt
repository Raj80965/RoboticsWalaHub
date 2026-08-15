package com.roboticswala.hub.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.roboticswala.hub.ui.screens.admin.AdminMainScreen
import com.roboticswala.hub.ui.screens.auth.login.LoginScreen
import com.roboticswala.hub.ui.screens.auth.pending.PendingApprovalScreen
import com.roboticswala.hub.ui.screens.auth.register.RegistrationScreen
import com.roboticswala.hub.ui.screens.splash.SplashScreen
import com.roboticswala.hub.ui.screens.student.StudentMainScreen

import com.google.firebase.auth.FirebaseAuth
import com.roboticswala.hub.data.models.UserProfile
import com.roboticswala.hub.ui.screens.student.attendance.StudentAttendanceHistoryScreen
import com.roboticswala.hub.ui.screens.student.attendance.StudentAttendanceViewModel
import com.roboticswala.hub.ui.screens.student.attendance.StudentAttendanceViewModelFactory
import com.roboticswala.hub.ui.screens.student.booking.StudentBookingsScreen
import com.roboticswala.hub.ui.screens.student.booking.StudentBookingViewModel
import com.roboticswala.hub.ui.screens.student.booking.StudentBookingViewModelFactory
import com.roboticswala.hub.ui.screens.student.projects.CreateProjectScreen
import com.roboticswala.hub.ui.screens.student.projects.ProjectDetailsScreen
import com.roboticswala.hub.ui.screens.student.qr.StudentQRScannerScreen
import com.roboticswala.hub.ui.screens.student.tasks.StudentTasksScreen
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // 1. Splash Screen (Session Auto-Check)
        composable(
            route = Screen.Splash.route,
            enterTransition = { fadeIn(animationSpec = tween(400)) },
            exitTransition = { fadeOut(animationSpec = tween(400)) }
        ) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToStudent = {
                    navController.navigate(Screen.StudentMain.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToAdmin = {
                    navController.navigate(Screen.AdminMain.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToPending = {
                    navController.navigate(Screen.PendingApproval.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // 2. Login Screen
        composable(
            route = Screen.Login.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            }
        ) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToStudent = {
                    navController.navigate(Screen.StudentMain.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToAdmin = {
                    navController.navigate(Screen.AdminMain.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToPendingApproval = {
                    navController.navigate(Screen.PendingApproval.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // 3. Registration Screen
        composable(
            route = Screen.Register.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            }
        ) {
            RegistrationScreen(
                onNavigateToLogin = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                onNavigateToPendingApproval = {
                    navController.navigate(Screen.PendingApproval.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // 4. Pending Approval Screen (Day 3)
        composable(
            route = Screen.PendingApproval.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            }
        ) {
            PendingApprovalScreen(
                onApproved = {
                    navController.navigate(Screen.StudentMain.route) {
                        popUpTo(Screen.PendingApproval.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.PendingApproval.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // 5. Student Main Dashboard
        composable(
            route = Screen.StudentMain.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            }
        ) {
            StudentMainScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.StudentMain.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToScanner = {
                    navController.navigate(Screen.StudentQRScanner.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToAttendanceHistory = {
                    navController.navigate(Screen.StudentAttendanceHistory.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToBookings = {
                    navController.navigate(Screen.StudentBookings.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToCreateProject = {
                    navController.navigate(Screen.CreateProject.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToProjectDetails = { projectId ->
                    navController.navigate(Screen.ProjectDetails.createRoute(projectId)) {
                        launchSingleTop = true
                    }
                },
                onNavigateToTasks = {
                    navController.navigate(Screen.StudentTasks.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // 6. Student QR Scanner Screen (Day 6)
        composable(
            route = Screen.StudentQRScanner.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            }
        ) {
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val currentUser = FirebaseAuth.getInstance().currentUser
            val studentProfile = UserProfile(
                uid = currentUid,
                fullName = currentUser?.displayName ?: "Student",
                email = currentUser?.email ?: "",
                status = "Approved"
            )
            val attendanceViewModel: StudentAttendanceViewModel = viewModel(
                factory = StudentAttendanceViewModelFactory(studentUid = currentUid)
            )

            StudentQRScannerScreen(
                studentProfile = studentProfile,
                viewModel = attendanceViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 7. Student Attendance History Screen (Day 6)
        composable(
            route = Screen.StudentAttendanceHistory.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            }
        ) {
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val attendanceViewModel: StudentAttendanceViewModel = viewModel(
                factory = StudentAttendanceViewModelFactory(studentUid = currentUid)
            )

            StudentAttendanceHistoryScreen(
                viewModel = attendanceViewModel,
                onNavigateToScanner = {
                    navController.navigate(Screen.StudentQRScanner.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 8. Student Lab Slot Bookings Screen (Day 7)
        composable(
            route = Screen.StudentBookings.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            }
        ) {
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val currentUser = FirebaseAuth.getInstance().currentUser
            val studentProfile = UserProfile(
                uid = currentUid,
                fullName = currentUser?.displayName ?: "Student",
                email = currentUser?.email ?: "",
                status = "Approved"
            )
            val bookingViewModel: StudentBookingViewModel = viewModel(
                factory = StudentBookingViewModelFactory(studentUid = currentUid)
            )

            StudentBookingsScreen(
                studentProfile = studentProfile,
                viewModel = bookingViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 9. Student Create Project Screen (Day 8)
        composable(
            route = Screen.CreateProject.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            }
        ) {
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val currentUser = FirebaseAuth.getInstance().currentUser
            val studentProfile = UserProfile(
                uid = currentUid,
                fullName = currentUser?.displayName ?: "Student",
                email = currentUser?.email ?: "",
                status = "Approved"
            )

            CreateProjectScreen(
                studentProfile = studentProfile,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onProjectCreated = {
                    navController.popBackStack()
                }
            )
        }

        // 10. Student Project Details & Updates Screen (Day 8)
        composable(
            route = Screen.ProjectDetails.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            }
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val currentUser = FirebaseAuth.getInstance().currentUser
            val studentProfile = UserProfile(
                uid = currentUid,
                fullName = currentUser?.displayName ?: "Student",
                email = currentUser?.email ?: "",
                status = "Approved"
            )

            ProjectDetailsScreen(
                projectId = projectId,
                currentUser = studentProfile,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 11. Student Assigned Tasks Screen (Day 9)
        composable(
            route = Screen.StudentTasks.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            }
        ) {
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val currentUser = FirebaseAuth.getInstance().currentUser
            val studentProfile = UserProfile(
                uid = currentUid,
                fullName = currentUser?.displayName ?: "Student",
                email = currentUser?.email ?: "",
                status = "Approved"
            )

            StudentTasksScreen(
                studentProfile = studentProfile,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 12. Admin Main Dashboard
        composable(
            route = Screen.AdminMain.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            }
        ) {
            AdminMainScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.AdminMain.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
