package com.example.pertemuan10.view

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pertemuan10.R
import com.example.pertemuan10.uicontroller.route.DestinasiDetail
import com.example.pertemuan10.viewmodel.DetailSiswa
import com.example.pertemuan10.viewmodel.DetailSiswaUiState
import com.example.pertemuan10.viewmodel.DetailViewModel
import com.example.pertemuan10.viewmodel.PenyediaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailSiswaScreen(
    navigateToEditItem: (Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.uiDetailState
    Scaffold(
        modifier = modifier,
        topBar = {
            SiswaTopAppBar(
                title = stringResource(id = DestinasiDetail.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToEditItem(uiState.detailSiswa.id) },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(id = R.string.update)
                )
            }
        }
    ) { innerPadding ->
        val coroutineScope = rememberCoroutineScope()
        BodyDetailDataSiswa(
            detailSiswaUiState = uiState,
            onRetry = viewModel::muatDetailSiswa,
            onDelete = {
                coroutineScope.launch {
                    viewModel.deleteSiswa()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .verticalScroll(state = rememberScrollState())
        )
    }
}

@Composable
private fun BodyDetailDataSiswa(
    detailSiswaUiState: DetailSiswaUiState,
    onRetry: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

        when {
            detailSiswaUiState.isLoading -> LoadingScreen(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.padding_large))
            )

            detailSiswaUiState.isError -> Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.gagal),
                    modifier = Modifier.padding(16.dp)
                )
                OutlinedButton(onClick = onRetry) {
                    Text(stringResource(R.string.retry))
                }
            }

            else -> {
                DetailDataSiswa(
                    detailSiswa = detailSiswaUiState.detailSiswa,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedButton(
                    onClick = { deleteConfirmationRequired = true },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.delete))
                }
            }
        }

        if (deleteConfirmationRequired) {
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    onDelete()
                },
                onDeleteCancel = { deleteConfirmationRequired = false },
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }
}

@Composable
fun DetailDataSiswa(
    detailSiswa: DetailSiswa, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier, colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
        ) {
            BarisDetailData(
                labelResID = R.string.nama1,
                itemDetail = detailSiswa.nama,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
            )
            BarisDetailData(
                labelResID = R.string.alamat1,
                itemDetail = detailSiswa.alamat,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
            )
            BarisDetailData(
                labelResID = R.string.telpon1,
                itemDetail = detailSiswa.telpon,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }
}

@Composable
private fun BarisDetailData(
    @StringRes labelResID: Int, itemDetail: String, modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(stringResource(labelResID))
        Spacer(modifier = Modifier.weight(1f))
        Text(text = itemDetail, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(stringResource(id = R.string.attention)) },
        text = { Text(stringResource(id = R.string.tanya)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(stringResource(id = R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(stringResource(R.string.yes))
            }
        }
    )
}
