package lv.ailab.senie.rest.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class StaticController {
    @GetMapping("/{page:(?:symbols|symbols_full)}")
    fun openStatic(@PathVariable("page") page: String): String = page

}