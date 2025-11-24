package com.example.playlistmaker.creator

import com.example.playlistmaker.data.dto.TrackDto

class Storage {

    private val trackList = listOf(
        TrackDto("Группа крови", "Кино", 265000, "url1"),
        TrackDto("Танцевать", "Ёлка", 222000, "url2"),
        TrackDto("Черный бумер", "Серега", 241000, "url3"),
        TrackDto("Звезда по имени Солнце", "Кино", 228000, "url4"),
        TrackDto("Вечно молодой", "Смысловые Галлюцинации", 259000, "url5"),
        TrackDto("Полковнику никто не пишет", "Би-2", 274000, "url6"),
        TrackDto("Прекрасное далёко", "Гости из Будущего", 231000, "url7"),
        TrackDto("Яндекс", "L'One", 190000, "url8"),
        TrackDto("Восьмиклассница", "Кино", 233000, "url9"),
        TrackDto("Моя бабушка курит трубку", "Гарик Сукачёв", 276000, "url10")
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