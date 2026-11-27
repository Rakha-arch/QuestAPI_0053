package com.example.pertemuan10.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pertemuan10.repositori.RepositoryDataSiswa
import com.example.pertemuan10.uicontroller.route.DestinasiDetail
import kotlinx.coroutines.launch

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositoryDataSiswa: RepositoryDataSiswa
) : ViewModel() {

    private val idSiswa: Int = checkNotNull(savedStateHandle[DestinasiDetail.itemIdArg])

    var uiDetailState by mutableStateOf(DetailSiswaUiState())
        private set

    init {
        muatDetailSiswa()
    }

    fun muatDetailSiswa() {
        viewModelScope.launch {
            uiDetailState = DetailSiswaUiState(isLoading = true)
            uiDetailState = try {
                val siswa = repositoryDataSiswa.getDataSiswaById(idSiswa)
                if (siswa != null) {
                    DetailSiswaUiState(detailSiswa = siswa.toDetailSiswa())
                } else {
                    DetailSiswaUiState(isError = true)
                }
            } catch (e: Exception) {
                DetailSiswaUiState(isError = true)
            }
        }
    }

    suspend fun deleteSiswa() {
        try {
            repositoryDataSiswa.deleteDataSiswa(uiDetailState.detailSiswa.toDataSiswa())
        } catch (e: Exception) {
            println("Gagal hapus data : ${e.message}")
        }
    }
}

data class DetailSiswaUiState(
    val detailSiswa: DetailSiswa = DetailSiswa(),
    val isLoading: Boolean = false,
    val isError: Boolean = false
)
