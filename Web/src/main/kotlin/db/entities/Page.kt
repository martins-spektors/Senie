package lv.ailab.senie.db.entities

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

/**
 * A partial/pseudo entity to use when extracting only the page data from the content table.
 */
@Entity
@Table(name = "content")
data class Page(
    // Not really an ID, obviously, but entities need a property marked as one,
    // and as long as we are only doing SELECT DISTINCT, it shouldn't matter that it's not actually unique.
    @Id val order: Int,
    val name: String?,
) {

    val linkName: String?
        get() = if (order == 0 && name == null) "_" else name

    val displayName: String?
        get() = if (linkName == "_") "Titullapa" else name
}