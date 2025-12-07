package com.example.playlistmaker.ui

enum class Screen(val route: String) {
    MAIN("main"),
    SEARCH("search"),
    SETTINGS("settings"),
    MEDIA_LIBRARY("media_library"),

    NEW_PLAYLIST("new_playlist"),

    FAVORITES("favorites"),

    TRACK_DETAILS("track_details"),

    PLAYLIST_DETAILS("playlist_details")
}