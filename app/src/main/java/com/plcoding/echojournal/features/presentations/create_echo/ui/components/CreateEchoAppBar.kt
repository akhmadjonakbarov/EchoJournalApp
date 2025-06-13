package com.plcoding.echojournal.features.presentations.create_echo.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.plcoding.echojournal.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEchoAppBar(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        navigationIcon = {
            Icon(
                modifier = Modifier.clickable(
                    onClick = onGoBack
                ),
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "",
            )
        },

        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.new_entry),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
    )
}