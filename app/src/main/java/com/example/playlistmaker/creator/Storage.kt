package com.example.playlistmaker.creator

import com.example.playlistmaker.data.dto.TrackDto

class Storage {

    private val trackList = listOf(
        TrackDto("1", "Группа крови", "Кино", 265000, "url1"),
        TrackDto("2", "Танцевать", "Ёлка", 222000, "url2"),
        TrackDto("3", "Черный бумер", "Серега", 241000, "url3"),
        TrackDto("4", "Звезда по имени Солнце", "Кино", 228000, "url4"),
        TrackDto("5", "Вечно молодой", "Смысловые Галлюцинации", 259000, "url5"),
        TrackDto("6", "Полковнику никто не пишет", "Би-2", 274000, "url6"),
        TrackDto("7", "Прекрасное далёко", "Гости из Будущего", 231000, "url7"),
        TrackDto("8", "Яндекс", "L'One", 190000, "url8"),
        TrackDto("9", "Восьмиклассница", "Кино", 233000, "url9"),
        TrackDto("10", "Моя бабушка курит трубку", "Гарик Сукачёв", 276000, "url10"),
        TrackDto("11", "Blinding Lights", "The Weeknd", 200000, "url11"),
        TrackDto("12", "Lose Yourself", "Eminem", 326000, "url12"),
        TrackDto("13", "Bohemian Rhapsody", "Queen", 354000, "url13"),
        TrackDto("14", "Smells Like Teen Spirit", "Nirvana", 301000, "url14"),
        TrackDto("15", "Billie Jean", "Michael Jackson", 294000, "url15"),
        TrackDto("16", "Shape of You", "Ed Sheeran", 233000, "url16"),
        TrackDto("17", "Rolling in the Deep", "Adele", 228000, "url17"),
        TrackDto("18", "Starboy", "The Weeknd", 230000, "url18"),
        TrackDto("19", "Without Me", "Eminem", 290000, "url19"),
        TrackDto("20", "Hotel California", "Eagles", 391000, "url20")
    )

    fun search(expression: String): List<TrackDto> {
        if (expression.isEmpty()) return emptyList()

        val normalizedQuery = expression.lowercase()

        return trackList.filter {
            it.trackName.lowercase().contains(normalizedQuery) ||
                    it.artistName.lowercase().contains(normalizedQuery)
        }
    }
}