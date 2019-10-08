package org.gotson.komga.interfaces.web.opds

import com.github.klinq.jpaspec.likeLower
import org.gotson.komga.domain.model.Book
import org.gotson.komga.domain.model.Library
import org.gotson.komga.domain.model.Series
import org.gotson.komga.domain.persistence.LibraryRepository
import org.gotson.komga.domain.persistence.SeriesRepository
import org.gotson.komga.interfaces.web.opds.dto.OpdsAuthor
import org.gotson.komga.interfaces.web.opds.dto.OpdsEntryAcquisition
import org.gotson.komga.interfaces.web.opds.dto.OpdsEntryNavigation
import org.gotson.komga.interfaces.web.opds.dto.OpdsFeed
import org.gotson.komga.interfaces.web.opds.dto.OpdsFeedAcquisition
import org.gotson.komga.interfaces.web.opds.dto.OpdsFeedNavigation
import org.gotson.komga.interfaces.web.opds.dto.OpdsLinkFeedNavigation
import org.gotson.komga.interfaces.web.opds.dto.OpdsLinkFileAcquisition
import org.gotson.komga.interfaces.web.opds.dto.OpdsLinkImage
import org.gotson.komga.interfaces.web.opds.dto.OpdsLinkImageThumbnail
import org.gotson.komga.interfaces.web.opds.dto.OpdsLinkPageStreaming
import org.gotson.komga.interfaces.web.opds.dto.OpdsLinkRel
import org.gotson.komga.interfaces.web.opds.dto.OpdsLinkSearch
import org.gotson.komga.interfaces.web.opds.dto.OpenSearchDescription
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.net.URI
import java.time.ZoneId
import java.time.ZonedDateTime

private const val ROUTE_BASE = "/opds/v1.2/"
private const val ROUTE_CATALOG = "catalog"
private const val ROUTE_SERIES_ALL = "series"
private const val ROUTE_SERIES_LATEST = "series/latest"
private const val ROUTE_LIBRARIES_ALL = "libraries"
private const val ROUTE_SEARCH = "search"

private const val ID_SERIES_ALL = "allSeries"
private const val ID_SERIES_LATEST = "latestSeries"
private const val ID_LIBRARIES_ALL = "allLibraries"

@RestController
@RequestMapping(value = [ROUTE_BASE], produces = [MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE])
class OpdsController(
    private val seriesRepository: SeriesRepository,
    private val libraryRepository: LibraryRepository
) {

  private val komgaAuthor = OpdsAuthor("Komga", URI("https://github.com/gotson/komga"))
  private val linkStart = OpdsLinkFeedNavigation(OpdsLinkRel.START, "${ROUTE_BASE}${ROUTE_CATALOG}")
  private val linkSearch = OpdsLinkSearch("${ROUTE_BASE}${ROUTE_SEARCH}")

  private val feedCatalog = OpdsFeedNavigation(
      id = "root",
      title = "Komga OPDS catalog",
      updated = ZonedDateTime.now(),
      author = komgaAuthor,
      links = listOf(
          OpdsLinkFeedNavigation(OpdsLinkRel.SELF, "${ROUTE_BASE}${ROUTE_CATALOG}"),
          linkStart,
          linkSearch
      ),
      entries = listOf(
          OpdsEntryNavigation(
              title = "All series",
              updated = ZonedDateTime.now(),
              id = ID_SERIES_ALL,
              content = "All series.",
              link = OpdsLinkFeedNavigation(OpdsLinkRel.SUBSECTION, "${ROUTE_BASE}${ROUTE_SERIES_ALL}")
          ),
          OpdsEntryNavigation(
              title = "Latest series",
              updated = ZonedDateTime.now(),
              id = ID_SERIES_LATEST,
              content = "Latest series.",
              link = OpdsLinkFeedNavigation(OpdsLinkRel.SUBSECTION, "${ROUTE_BASE}${ROUTE_SERIES_LATEST}")
          ),
          OpdsEntryNavigation(
              title = "All libraries",
              updated = ZonedDateTime.now(),
              id = ID_LIBRARIES_ALL,
              content = "All libraries.",
              link = OpdsLinkFeedNavigation(OpdsLinkRel.SUBSECTION, "${ROUTE_BASE}${ROUTE_LIBRARIES_ALL}")
          )
      )
  )

  private val openSearchDescription = OpenSearchDescription(
      shortName = "Search",
      description = "Search for series",
      url = OpenSearchDescription.OpenSearchUrl("${ROUTE_BASE}${ROUTE_SERIES_ALL}?search={searchTerms}")
  )

  @GetMapping(ROUTE_CATALOG)
  fun getCatalog(): OpdsFeed = feedCatalog

  @GetMapping(ROUTE_SEARCH)
  fun getSearch(): OpenSearchDescription = openSearchDescription

  @GetMapping(ROUTE_SERIES_ALL)
  fun getAllSeries(
      @RequestParam("search")
      searchTerm: String?
  ): OpdsFeed {
    val sort = Sort.by(Sort.Order.asc("name").ignoreCase())
    val series = if (!searchTerm.isNullOrEmpty()) {
      val spec = Series::name.likeLower("%$searchTerm%")
      seriesRepository.findAll(spec, sort)
    } else {
      seriesRepository.findAll(sort)
    }

    return OpdsFeedNavigation(
        id = ID_SERIES_ALL,
        title = "All series",
        updated = ZonedDateTime.now(),
        author = komgaAuthor,
        links = listOf(
            OpdsLinkFeedNavigation(OpdsLinkRel.SELF, "${ROUTE_BASE}${ROUTE_SERIES_ALL}"),
            linkStart
        ),
        entries = series.map { it.toOpdsEntry() }
    )
  }

  @GetMapping(ROUTE_SERIES_LATEST)
  fun getLatestSeries(): OpdsFeed {
    val series = seriesRepository.findAll(Sort(Sort.Direction.DESC, "lastModifiedDate"))
    return OpdsFeedNavigation(
        id = ID_SERIES_LATEST,
        title = "Latest series",
        updated = ZonedDateTime.now(),
        author = komgaAuthor,
        links = listOf(
            OpdsLinkFeedNavigation(OpdsLinkRel.SELF, "${ROUTE_BASE}${ROUTE_SERIES_LATEST}"),
            linkStart
        ),
        entries = series.map { it.toOpdsEntry() }
    )
  }

  @GetMapping(ROUTE_LIBRARIES_ALL)
  fun getLibraries(): OpdsFeed {
    val libraries = libraryRepository.findAll()
    return OpdsFeedNavigation(
        id = ID_LIBRARIES_ALL,
        title = "All libraries",
        updated = ZonedDateTime.now(),
        author = komgaAuthor,
        links = listOf(
            OpdsLinkFeedNavigation(OpdsLinkRel.SELF, "${ROUTE_BASE}${ROUTE_LIBRARIES_ALL}"),
            linkStart
        ),
        entries = libraries.map { it.toOpdsEntry() }
    )
  }

  @GetMapping("series/{id}")
  fun getOneSeries(
      @PathVariable id: Long
  ): OpdsFeed =
      seriesRepository.findByIdOrNull(id)?.let { series ->
        OpdsFeedAcquisition(
            id = series.id.toString(),
            title = series.name,
            updated = series.lastModifiedDate?.atZone(ZoneId.systemDefault()) ?: ZonedDateTime.now(),
            author = komgaAuthor,
            links = listOf(
                OpdsLinkFeedNavigation(OpdsLinkRel.SELF, "${ROUTE_BASE}series/$id"),
                linkStart
            ),
            entries = series.books.map { it.toOpdsEntry() }
        )
      } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

  @GetMapping("libraries/{id}")
  fun getOneLibrary(
      @PathVariable id: Long
  ): OpdsFeed =
      libraryRepository.findByIdOrNull(id)?.let { library ->
        OpdsFeedNavigation(
            id = library.id.toString(),
            title = library.name,
            updated = library.lastModifiedDate?.atZone(ZoneId.systemDefault()) ?: ZonedDateTime.now(),
            author = komgaAuthor,
            links = listOf(
                OpdsLinkFeedNavigation(OpdsLinkRel.SELF, "${ROUTE_BASE}libraries/$id"),
                linkStart
            ),
            entries = seriesRepository.findByLibraryId(library.id, Sort.by(Sort.Order.asc("name").ignoreCase())).map { it.toOpdsEntry() }
        )
      } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)



  private fun Series.toOpdsEntry() =
      OpdsEntryNavigation(
          title = name,
          updated = lastModifiedDate?.atZone(ZoneId.systemDefault()) ?: ZonedDateTime.now(),
          id = id.toString(),
          content = "",
          link = OpdsLinkFeedNavigation(OpdsLinkRel.SUBSECTION, "${ROUTE_BASE}series/$id")
      )

  private fun Book.toOpdsEntry() =
      OpdsEntryAcquisition(
          title = name,
          updated = lastModifiedDate?.atZone(ZoneId.systemDefault()) ?: ZonedDateTime.now(),
          id = id.toString(),
          content = "",
          links = listOf(
              OpdsLinkImageThumbnail("image/png", "/api/v1/series/${series.id}/books/$id/thumbnail"),
              OpdsLinkImage(metadata.pages[0].mediaType, "/api/v1/series/${series.id}/books/$id/pages/1"),
              OpdsLinkFileAcquisition(metadata.mediaType
                  ?: "application/octet-stream", "/api/v1/series/${series.id}/books/$id/file"),
              OpdsLinkPageStreaming("image/jpeg", "/api/v1/series/${series.id}/books/$id/pages/{pageNumber}?convert=jpeg&amp;zero_based=true", metadata.pages.size)
          )
      )

  private fun Library.toOpdsEntry(): OpdsEntryNavigation {
    return OpdsEntryNavigation(
        title = name,
        updated = lastModifiedDate?.atZone(ZoneId.systemDefault()) ?: ZonedDateTime.now(),
        id = id.toString(),
        content = "",
        link = OpdsLinkFeedNavigation(OpdsLinkRel.SUBSECTION, "${ROUTE_BASE}libraries/$id")
    )
  }

}
