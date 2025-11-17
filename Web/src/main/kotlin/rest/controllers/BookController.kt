package lv.ailab.senie.rest.controllers

import lv.ailab.senie.db.repositories.BookRepository
import lv.ailab.senie.db.repositories.ContentRepository
import lv.ailab.senie.db.repositories.PageRepository
import lv.ailab.senie.rest.CommonFailures
import lv.ailab.senie.rest.CommonFailures.bookNotFound
import lv.ailab.senie.utils.urlEncode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.view.RedirectView

@Controller
class BookController(
    private val bookRepo: BookRepository,
    private val contentRepo: ContentRepository,
    private val pageRepo: PageRepository,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Redirects to the first book in the collection.
     */
    @GetMapping("/collections/{code}")
    fun collection(@PathVariable("code") code: String): RedirectView {
        // Getting the first book only could be one in SQL/JPQL, but collections are small and this is simpler
        val book = bookRepo.findAllInCollection(code).sortedBy { it.orderInCollection }.first()
        return RedirectView("/books/${book.fullSource.urlEncode()}")
    }

    /**
     * Renders the main book view.
     */
    @GetMapping("/books/{code}")
    fun bookPage(
        @PathVariable("code") source: String,
        @RequestParam("page") pageParam: String?,
        model: Model,
    ): String {
        // Book / collection
        val book = bookRepo.findByFullSource(source) ?: throw bookNotFound(source)
        val collection = book.collectionCode?.let(bookRepo::findCollection)
        model.addAttribute("pageTitle", collection?.displayTitle ?: book.displayTitle)
        if (collection != null) {
            model.addAttribute("collection", collection)
            model.addAttribute(
                "books",
                bookRepo.findAllInCollection(book.collectionCode).sortedBy { it.orderInCollection },
            )
        }
        model.addAttribute("currentBook", book)

        // Pages
        val pages = pageRepo.findAllPagesInBook(book.fullSource).map { page ->
            if (page.order == 0)
                page.name = "0"
            page
        }.sortedBy { it.order }
        val hasPages = pages.any { it.order > 1 }
        val currentPage = pageParam?.let { pageName ->
            pages.firstOrNull { page -> page.name == pageName }
                ?: throw CommonFailures.pageNotFound(pageName, book.fullSource)
        } ?: pages.first()
        val prevPage = pages[(pages.indexOf(currentPage) - 1).coerceAtLeast(0)]
            .takeUnless { it == currentPage }
        val nextPage = pages[(pages.indexOf(currentPage) + 1).coerceAtMost(pages.lastIndex)]
            .takeUnless { it == currentPage }
        if (hasPages) {
            model.addAttribute("prevPage", prevPage)
            model.addAttribute("nextPage", nextPage)
            model.addAttribute("pages", pages)
            model.addAttribute("currentPage", currentPage)
        }

        // Content
        val displayPages =
            if (hasPages) listOf(currentPage.order)
            else (0..1).toList()
        val lines = contentRepo.findAllBySourceAndPageSortOrderIn(book.fullSource, displayPages)

        model.addAttribute("lines", lines)

        return "book"
    }

}