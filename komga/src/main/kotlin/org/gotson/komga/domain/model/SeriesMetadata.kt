package org.gotson.komga.domain.model

import java.time.LocalDateTime

class SeriesMetadata(
  val status: Status = Status.ONGOING,
  title: String,
  titleSort: String = title,
  summary: String = "",
  val readingDirection: ReadingDirection? = null,
  publisher: String = "",
  val ageRating: Int? = null,
  language: String = "",
  genres: Set<String> = emptySet(),
  tags: Set<String> = emptySet(),
  val totalBookCount: Int? = null,

  val statusLock: Boolean = false,
  val titleLock: Boolean = false,
  val titleSortLock: Boolean = false,
  val summaryLock: Boolean = false,
  val readingDirectionLock: Boolean = false,
  val publisherLock: Boolean = false,
  val ageRatingLock: Boolean = false,
  val languageLock: Boolean = false,
  val genresLock: Boolean = false,
  val tagsLock: Boolean = false,
  val totalBookCountLock: Boolean = false,

  val seriesId: String = "",

  override val createdDate: LocalDateTime = LocalDateTime.now(),
  override val lastModifiedDate: LocalDateTime = createdDate,
) : Auditable() {
  val title = title.trim()
  val titleSort = titleSort.trim()
  val summary = summary.trim()
  val publisher = publisher.trim()
  val language = language.trim().lowercase()
  val tags = tags.map { it.lowercase().trim() }.filter { it.isNotBlank() }.toSet()
  val genres = genres.map { it.lowercase().trim() }.filter { it.isNotBlank() }.toSet()

  fun copy(
    status: Status = this.status,
    title: String = this.title,
    titleSort: String = this.titleSort,
    summary: String = this.summary,
    readingDirection: ReadingDirection? = this.readingDirection,
    publisher: String = this.publisher,
    ageRating: Int? = this.ageRating,
    language: String = this.language,
    genres: Set<String> = this.genres,
    tags: Set<String> = this.tags,
    totalBookCount: Int? = this.totalBookCount,
    statusLock: Boolean = this.statusLock,
    titleLock: Boolean = this.titleLock,
    titleSortLock: Boolean = this.titleSortLock,
    summaryLock: Boolean = this.summaryLock,
    readingDirectionLock: Boolean = this.readingDirectionLock,
    publisherLock: Boolean = this.publisherLock,
    ageRatingLock: Boolean = this.ageRatingLock,
    languageLock: Boolean = this.languageLock,
    genresLock: Boolean = this.genresLock,
    tagsLock: Boolean = this.tagsLock,
    totalBookCountLock: Boolean = this.totalBookCountLock,
    seriesId: String = this.seriesId,
    createdDate: LocalDateTime = this.createdDate,
    lastModifiedDate: LocalDateTime = this.lastModifiedDate,
  ) =
    SeriesMetadata(
      status = status,
      title = title,
      titleSort = titleSort,
      summary = summary,
      readingDirection = readingDirection,
      publisher = publisher,
      ageRating = ageRating,
      language = language,
      genres = genres,
      tags = tags,
      totalBookCount = totalBookCount,
      statusLock = statusLock,
      titleLock = titleLock,
      titleSortLock = titleSortLock,
      summaryLock = summaryLock,
      readingDirectionLock = readingDirectionLock,
      publisherLock = publisherLock,
      ageRatingLock = ageRatingLock,
      languageLock = languageLock,
      genresLock = genresLock,
      tagsLock = tagsLock,
      totalBookCountLock = totalBookCountLock,
      seriesId = seriesId,
      createdDate = createdDate,
      lastModifiedDate = lastModifiedDate,
    )

  enum class Status {
    ENDED, ONGOING, ABANDONED, HIATUS
  }

  enum class ReadingDirection {
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT,
    VERTICAL,
    WEBTOON
  }

  override fun toString(): String {
    return "SeriesMetadata(status=$status, readingDirection=$readingDirection, ageRating=$ageRating, language='$language', totalBookCount=$totalBookCount, statusLock=$statusLock, titleLock=$titleLock, titleSortLock=$titleSortLock, summaryLock=$summaryLock, readingDirectionLock=$readingDirectionLock, publisherLock=$publisherLock, ageRatingLock=$ageRatingLock, languageLock=$languageLock, genresLock=$genresLock, tagsLock=$tagsLock, totalBookCountLock=$totalBookCountLock, seriesId='$seriesId', createdDate=$createdDate, lastModifiedDate=$lastModifiedDate, title='$title', titleSort='$titleSort', summary='$summary', publisher='$publisher', tags=$tags, genres=$genres)"
  }
}
