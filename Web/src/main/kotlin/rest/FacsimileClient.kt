package lv.ailab.senie.rest

import lv.ailab.senie.db.entities.Book
import lv.ailab.senie.db.entities.Page
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class FacsimileClient(
    restClientBuilder: RestClient.Builder,

    @Value($$"${senie.data.base-url}")
    dataUrl: String,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    val baseUrl = "${dataUrl}/facsimiles"

    val restClient by lazy {
        restClientBuilder
            // 404 are expected, so do nothing (don't throw exceptions)
            .defaultStatusHandler(HttpStatus.NOT_FOUND::equals) { _, _ -> }
            .build()
    }

    /**
     * Finds the facsimile image URL for the [page] in [book], trying every known variant.
     *
     * @return Image URL for the facsimile, or `null` if none found.
     */
    fun findUrlFor(book: Book, page: Page): String? {
        // Page numbers in image files may be any of these lengths (zero-padded)
        val lengths = 1..3
        // Page images may have any of these file extensions
        val extensions = listOf("jpg", "png")

        val permutations =
            lengths.flatMap { length ->
                extensions.map { ext ->
                    Pair(length, ext)
                }
            }
        val dirUrl = "$baseUrl/${book.fullSource.replace("::", "/")}/${book.itemCode}"

        logger.debug("Looking for facsimile image for ${book.fullSource}, page ${page.displayName} (${page.order})...")
        return permutations.firstNotNullOfOrNull { (length, extension) ->
            val fileName = "${page.displayName}".padStart(length, '0') + ".$extension"
            val fileUrl = "$dirUrl/$fileName"
            val isFound = restClient
                .head().uri(fileUrl)
                .retrieve()
                .toBodilessEntity().statusCode.is2xxSuccessful
            fileUrl.takeIf { isFound }
                .also {
                    if (isFound) logger.debug("200 : $fileUrl")
                    else logger.debug("404 : $fileUrl")
                }
        }
    }
}