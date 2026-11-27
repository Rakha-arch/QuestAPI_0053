package com.example.pertemuan10.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pertemuan10.repositori.RepositoryDataSiswa
import com.example.pertemuan10.uicontroller.route.DestinasiEdit
import kotlinx.coroutines.launch

class EditViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositoryDataSiswa: RepositoryDataSiswa
) : ViewModel() {

    private val idSiswa: Int = checkNotNull(savedStateHandle[DestinasiEdit.itemIdArg])

    var uiStateSiswa by mutableStateOf(UIStateSiswa())
        private set

    init {
        muatDataSiswa()
    }

    private fun muatDataSiswa() {
        viewModelScope.launch {
            try {
                val siswa = repositoryDataSiswa.getDataSiswaById(idSiswa)
                if (siswa != null) {
                    uiStateSiswa = siswa.toUiStateSiswa(isEntryValid = true)
                }
            } catch (e: Exception) {
                println("Gagal memuat data : ${e.message}")
            }
        }
    }

    fun updateUiState(detailSiswa: DetailSiswa) {
        uiStateSiswa =
            UIStateSiswa(detailSiswa = detailSiswa, isEntryValid = validasiInput(detailSiswa))
    }

    private fun validasiInput(uiState: DetailSiswa = uiStateSiswa.detailSiswa): Boolean {
        return with(uiState) {
            nama.isNotBlank() && alamat.isNotBlank() && telpon.isNotBlank()
        }
    }

    suspend fun updateSiswa() {
        if (validasiInput()) {
            try {
                repositoryDataSiswa.updateDataSiswa(uiStateSiswa.detailSiswa.toDataSiswa())
            } catch (e: Exception) {
                println("Gagal update data : ${e.message}")
            }
        }
    }
}
