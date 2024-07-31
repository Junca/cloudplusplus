package nl.cloudplusplus.evaluation.models

import kotlinx.serialization.Serializable

@Serializable
data class Section (
    var title: String,
    var from: Int,
    var to: Int,
    var index: Int,
    var uuid: String,
)