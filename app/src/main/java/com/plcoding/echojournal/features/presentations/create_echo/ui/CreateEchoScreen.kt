package com.plcoding.echojournal.features.presentations.create_echo.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plcoding.echojournal.R
import com.plcoding.echojournal.core.presentation.designsystem.buttons.PrimaryButton
import com.plcoding.echojournal.core.presentation.designsystem.buttons.SecondaryButton
import com.plcoding.echojournal.core.presentation.designsystem.textfields.TransparentHintTextField
import com.plcoding.echojournal.core.presentation.designsystem.theme.EchoJournalTheme
import com.plcoding.echojournal.core.presentation.designsystem.theme.secondary70
import com.plcoding.echojournal.core.presentation.designsystem.theme.secondary95
import com.plcoding.echojournal.core.util.ObserveAsEvents
import com.plcoding.echojournal.features.presentations.components.play.MoodPlayer
import com.plcoding.echojournal.features.presentations.create_echo.logic.CreateEchoAction
import com.plcoding.echojournal.features.presentations.create_echo.logic.CreateEchoEvent
import com.plcoding.echojournal.features.presentations.create_echo.logic.CreateEchoState
import com.plcoding.echojournal.features.presentations.create_echo.logic.CreateEchoViewModel
import com.plcoding.echojournal.features.presentations.create_echo.ui.components.CreateEchoAppBar
import com.plcoding.echojournal.features.presentations.create_echo.ui.components.SelectMoodSheet
import com.plcoding.echojournal.features.presentations.create_echo.ui.components.TopicsFlowRow
import com.plcoding.echojournal.features.presentations.models.MoodUi
import com.plcoding.echojournal.features.presentations.models.PlaybackState
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateEchoRoot(
    viewModel: CreateEchoViewModel = koinViewModel(), onConfirmLeave: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val ctx = LocalContext.current

    ObserveAsEvents(
        viewModel.events
    ) { event ->
        when (event) {
            CreateEchoEvent.FailedToSaveFile -> {
                Toast.makeText(
                    ctx, ctx.getText(R.string.error_couldnt_save_file), Toast.LENGTH_LONG
                ).show()
            }

            CreateEchoEvent.FileSaveSuccessfully -> {
                Toast.makeText(
                    ctx, ctx.getText(R.string.recording_was_saved), Toast.LENGTH_LONG
                ).show()
                onConfirmLeave()
            }
        }
    }

    CreateEchoScreen(
        state = state, onAction = viewModel::onAction, onConfirmLeave = onConfirmLeave
    )
}

@Composable
fun CreateEchoScreen(
    state: CreateEchoState, onAction: (CreateEchoAction) -> Unit, onConfirmLeave: () -> Unit
) {
    BackHandler(
        enabled = !state.showConfirmLeaveDialog
    ) {
        onAction(CreateEchoAction.OnGoBack)

    }


    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface, topBar = {
            CreateEchoAppBar(
                onGoBack = {
                    onAction(CreateEchoAction.OnNavigateBackClick)
                })
        }) { innerPadding ->
        val descriptionFocusRequester = remember {
            FocusRequester()
        }
        val focusManager = LocalFocusManager.current
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.mood == null) {
                    FilledIconButton(
                        onClick = {
                            onAction(CreateEchoAction.OnSelectMoodClick)
                        }, colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondary95,
                            contentColor = MaterialTheme.colorScheme.secondary70
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = stringResource(R.string.add_mood)
                        )
                    }
                } else {
                    Image(
                        imageVector = ImageVector.vectorResource(state.mood.iconSet.fill),
                        contentDescription = state.mood.title.asString(),
                        modifier = Modifier
                            .height(32.dp)
                            .clickable {
                                onAction(CreateEchoAction.OnSelectMoodClick)
                            },
                        contentScale = ContentScale.FillHeight
                    )
                }

                TransparentHintTextField(
                    text = state.titleText,
                    onValueChange = {
                        onAction(CreateEchoAction.OnTitleTextChange(it))
                    },
                    modifier = Modifier.weight(1f),
                    hintText = stringResource(R.string.add_title),
                    textStyle = MaterialTheme.typography.headlineLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            descriptionFocusRequester.requestFocus()
                        })
                )
            }

            MoodPlayer(
                moodUi = state.mood,
                playbackState = PlaybackState.PAUSED,
                playerProgress = { 0f },
                durationPlayed = state.durationPlayed,
                totalPlaybackDuration = state.playbackTotalDuration,
                powerRatios = state.playbackAmplitudes,
                onPlayClick = {
                    onAction(CreateEchoAction.OnPlayAudioClick)
                },
                onPauseClick = {
                    onAction(CreateEchoAction.OnPauseAudioClick)
                },
                onTrackSizeAvailable = {
                    onAction(CreateEchoAction.OnTrackSizeAvailable(it))
                })

            TopicsFlowRow(
                topics = state.topics,
                addTopicText = state.addTopicText,
                showCreateTopicOption = state.showCreateTopicOption,
                showTopicSuggestions = state.showTopicSuggestions,
                searchResults = state.searchResults,
                onTopicClick = {
                    onAction(CreateEchoAction.OnTopicClick(it))
                },
                onDismissTopicSuggestions = {
                    onAction(CreateEchoAction.OnDismissTopicSuggestions)
                },
                onRemoveTopicClick = {
                    onAction(CreateEchoAction.OnRemoveTopicClick(it))
                },
                onAddTopicTextChange = {
                    onAction(CreateEchoAction.OnAddTopicTextChange(it))
                })

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Create,
                    contentDescription = stringResource(R.string.add_description),
                    tint = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.size(16.dp)
                )
                TransparentHintTextField(
                    text = state.noteText,
                    onValueChange = {
                        onAction(CreateEchoAction.OnNoteTextChange(it))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(descriptionFocusRequester),
                    hintText = stringResource(R.string.add_description),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SecondaryButton(
                    text = stringResource(R.string.cancel), onClick = {
                        onAction(CreateEchoAction.OnCancelClick)
                    }, modifier = Modifier.fillMaxHeight()
                )
                PrimaryButton(text = stringResource(R.string.save), onClick = {
                    onAction(CreateEchoAction.OnSaveClick)
                }, modifier = Modifier.weight(1f), enabled = state.canSaveEcho, leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.save),
                        modifier = Modifier.size(16.dp)
                    )
                })
            }
        }
    }

    if (state.showMoodSelector) {
        SelectMoodSheet(
            onDismiss = {
                onAction(CreateEchoAction.OnDismissMoodSelector)
            },

            selectedMood = state.selectedMood, onMoodClick = {
                onAction(CreateEchoAction.OnMoodClick(it))
            }, onConfirmClick = {
                onAction(CreateEchoAction.OnConfirmMood)
            })
    }

    if (state.showConfirmLeaveDialog) {
        AlertDialog(title = {
            Text(
                text = stringResource(R.string.discard_recording)
            )
        }, text = {
            Text(
                text = stringResource(R.string.this_cannot_be_undone)
            )
        }, onDismissRequest = {
            onAction(CreateEchoAction.OnDismissConfirmLeaveDialog)
        }, confirmButton = {
            TextButton(
                onClick = onConfirmLeave
            ) {
                Text(
                    text = stringResource(R.string.discard),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }, dismissButton = {
            TextButton(
                onClick = {
                    onAction(CreateEchoAction.OnDismissConfirmLeaveDialog)
                }) {
                Text(
                    text = stringResource(R.string.cancel)
                )
            }
        })
    }
}

@Preview
@Composable
private fun Preview() {
    EchoJournalTheme {
        CreateEchoScreen(
            state = CreateEchoState(
                mood = MoodUi.SAD,
                titleText = "",
                noteText = "",
                canSaveEcho = true,
                showMoodSelector = false,
                topics = listOf<String>("Love", "Happy")
            ), onAction = {}, onConfirmLeave = {})
    }
}