package nl.cloudplusplus.evaluation.models

import kotlinx.serialization.Serializable

@Serializable
data class Option (
    var label: String,
    var value: String,
)