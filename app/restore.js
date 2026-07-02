const fs = require('fs');
const file = '/app/src/main/java/com/example/ui/screens/AuraScreens.kt';
let content = fs.readFileSync(file, 'utf8');

const startMarker = "// --- 2. Fullscreen Video & Reel Creator Overlay ---";
const endMarker = 'modifier = Modifier.testTag("main_screen")\\n        ) { innerPadding ->';

const startIndex = content.indexOf(startMarker);
if (startIndex === -1) {
    console.error("Start marker not found");
    process.exit(1);
}

// Find the line with testTag("main_screen")
const tagIndex = content.indexOf('modifier = Modifier.testTag("main_screen")', startIndex);
if (tagIndex === -1) {
    console.error("Tag marker not found");
    process.exit(1);
}

// Find the index of ") { innerPadding ->" right after that tag
const endIndex = content.indexOf(") { innerPadding ->", tagIndex) + ") { innerPadding ->".length;

const restoredText = `        },
                                        onClick = {
                                            showPlusDropdown = false
                                            viewModel.setReelCreatorVisible(true)
                                        }
                                    )
                                    Divider(color = Color(0xFFF0F2F5), thickness = 0.5.dp)

                                    // Option 5: Note
                                    DropdownMenuItem(
                                        text = { 
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Outlined.ChatBubble,
                                                    contentDescription = "Note option",
                                                    tint = Color(0xFFFFB300),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text("Note", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                                            }
                                        },
                                        onClick = {
                                            showPlusDropdown = false
                                            viewModel.setNoteCreatorVisible(true)
                                        }
                                    )

                                    Divider(color = Color(0xFFF0F2F5), thickness = 0.5.dp)

                                    // Option 6: Aura Match & Vibe Check 💫
                                    DropdownMenuItem(
                                        text = { 
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .background(
                                                        brush = Brush.horizontalGradient(
                                                            colors = listOf(Color(0xFFE040FB).copy(alpha = 0.15f), Color(0xFF00E5FF).copy(alpha = 0.15f))
                                                        ),
                                                        shape = RoundedCornerShape(8.dp)
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                            ) {
                                                Text("💫", fontSize = 14.sp)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Vibe Check Match Checking",
                                                     fontWeight = FontWeight.ExtraBold,
                                                     color = Color(0xFF7C4DFF),
                                                     fontSize = 12.5.sp
                                                )
                                            }
                                        },
                                        onClick = {
                                            showPlusDropdown = false
                                            viewModel.setVibeCheckSimulatorVisible(true)
                                        }
                                    )
                                }
                            }

                            // Action 2: Search Filter Button
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF0F2F5))
                                    .clickable {
                                        if (state.isViewingAsGuest) {
                                            android.widget.Toast.makeText(
                                                context,
                                                "আপনি বর্তমানে গেস্ট মোডে আছেন! বন্ধুদের খুঁজতে ও অ্যাক্টিভিটি করতে দয়া করে ওড়া অ্যাপে রেজিস্টার করুন। 🔑🔐\\n(Guest mode restricted: Please register/sign up for full access!)",
                                                android.widget.Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            viewModel.navigateTo(Screen.SearchUsers)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                             // Action 3: Chat / Messenger Button without live badge
                             Box(
                                 modifier = Modifier
                                     .size(38.dp)
                                     .clip(CircleShape)
                                     .background(Color(0xFFF0F2F5))
                                     .clickable {
                                         if (state.isViewingAsGuest) {
                                             android.widget.Toast.makeText(
                                                 context,
                                                 "আপনি বর্তমানে গেস্ট মোডে আছেন! বন্ধুদের সাথে চ্যাট করতে ওড়া অ্যাপে রেজিস্টার করুন। 🔑🔐\\n(Guest mode restricted: Please register/sign up for full access!)",
                                                 android.widget.Toast.LENGTH_LONG
                                             ).show()
                                         } else {
                                             val other = viewModel.allUsers.value.find { !it.isCurrentUser }
                                             if (other != null) {
                                                 viewModel.navigateTo(Screen.ChatRoom(other))
                                             }
                                         }
                                     }
                                     .testTag("chat_trigger"),
                                 contentAlignment = Alignment.Center
                             ) {
                                 Icon(
                                     imageVector = Icons.Outlined.ChatBubbleOutline,
                                     contentDescription = "Aura Messaging",
                                     tint = Color.Black,
                                     modifier = Modifier.size(19.dp)
                                 )
                             }
                        }
                    }

                    Divider(color = Color(0xFFF0F2F5), thickness = 0.5.dp)
                }
                }
            },
            bottomBar = {
                if (state.showStoryViewer == null) {
                    AnimatedVisibility(
                        visible = isBottomBarVisible,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                    ) {
                        NavigationBar(
                            containerColor = Color.White,
                            tonalElevation = 8.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp)
                                .testTag("bottom_navigation_bar"),
                            windowInsets = WindowInsets.navigationBars
                        ) {
                            val tabs = listOf(
                                Triple(MainTab.FEEDS, Icons.Filled.Home to Icons.Outlined.Home, "Home"),
                                Triple(MainTab.FRIENDS, Icons.Filled.People to Icons.Outlined.People, "Friends"),
                                Triple(MainTab.VIDEOS, Icons.Filled.OndemandVideo to Icons.Outlined.OndemandVideo, "Videos"),
                                Triple(MainTab.NOTIFICATIONS, Icons.Filled.Notifications to Icons.Outlined.Notifications, "Notification"),
                                Triple(MainTab.PROFILE, Icons.Filled.AccountCircle to Icons.Outlined.AccountCircle, "Profile")
                            )

                            tabs.forEach { (tab, icons, label) ->
                                val isSelected = if (state.currentTab == MainTab.CREATE_POST) {
                                    tab == MainTab.FEEDS
                                } else {
                                    state.currentTab == tab
                                }
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = {
                                        if (state.isViewingAsGuest && tab != MainTab.FEEDS) {
                                            android.widget.Toast.makeText(
                                                context,
                                                "আপনি বর্তমানে গেস্ট মোডে আছেন! এই ট্যাবটি অ্যাক্সেস করতে এবং বন্ধুদের সাথে সংযুক্ত হতে ওড়া অ্যাপে রেজিস্টার করুন। 🔑🔐\\n(Guest mode restricted: Please register/sign up for full access!)",
                                                android.widget.Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            viewModel.selectTab(tab)
                                        }
                                    },
                                    icon = {
                                        if (tab == MainTab.PROFILE) {
                                            ProfileAvatar(
                                                avatarId = currentUserState?.avatarUrl ?: "avatar_user_main",
                                                fallbackName = currentUserState?.displayName,
                                                size = 28,
                                                modifier = Modifier.border(
                                                    width = if (isSelected) 2.dp else 1.dp,
                                                    color = if (isSelected) LavenderPrimary else Color.Gray.copy(alpha = 0.5f),
                                                    shape = CircleShape
                                                )
                                            )
                                        } else {
                                            Icon(
                                                imageVector = if (isSelected) icons.first else icons.second,
                                                contentDescription = label,
                                                tint = if (isSelected) LavenderPrimary else Color.Gray
                                            )
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = label,
                                            color = if (isSelected) LavenderPrimary else Color.Gray,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    alwaysShowLabel = true,
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color.Transparent
                                    )
                                )
                            }
                        }
                    }
                }
            },
            containerColor = Color.White,
            modifier = Modifier.testTag("main_screen")
        ) { innerPadding ->`;

content = content.substring(0, startIndex) + restoredText + content.substring(endIndex);
fs.writeFileSync(file, content, 'utf8');
console.log("Restoration successful!");
