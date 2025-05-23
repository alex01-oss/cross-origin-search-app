package com.loc.searchapp.core.domain.model.catalog

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class EquipmentModel(
    @SerialName("name_equipment") val nameEquipment: String,
    @SerialName("producer_name") val producerName: String,
) : Parcelable
