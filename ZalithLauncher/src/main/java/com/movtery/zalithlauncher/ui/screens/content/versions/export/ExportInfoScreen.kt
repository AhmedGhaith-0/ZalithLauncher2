/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.ui.screens.content.versions.export

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.download.assets.platform.Platform
import com.movtery.zalithlauncher.game.version.export.ExportInfo
import com.movtery.zalithlauncher.game.version.export.PackEditOptions
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.components.AnimatedLazyColumn
import com.movtery.zalithlauncher.ui.components.WarningCard
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.CardPosition
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.IntSliderSettingsCard
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.SwitchSettingsCard
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.TextInputSettingsCard
import com.movtery.zalithlauncher.utils.platform.getMaxMemoryForSettings
import com.movtery.zalithlauncher.utils.string.isEmptyOrBlank
import com.movtery.zalithlauncher.utils.string.isNotEmptyOrBlank
import com.movtery.zalithlauncher.utils.string.toSingleLine

private const val OPTIONAL_AUTHOR = "requireAuthor"
private const val OPTION_DESCRIPTION = "requireDescription"
private const val OPTIONAL_JVM_ARGS = "requireJvmArgs"
private const val OPTIONAL_JAVA_ARGS = "requireJavaArgs"
private const val OPTIONAL_WEBSITE_URL = "requireWebsiteUrl"
private const val OPTIONAL_MIN_MEMORY = "requireMinMemory"
private const val OPTIONAL_MAX_MEMORY = "requireMaxMemory"
private const val OPTIONAL_PACK_REMOTE = "requirePackRemote"

@Composable
private fun rememberOptionalItemIDList(
    options: PackEditOptions
): List<String> {
    return remember(options) {
        buildList {
            if (options.requireAuthor) add(OPTIONAL_AUTHOR)
            add(OPTION_DESCRIPTION)
            if (options.requireJvmArgs) add(OPTIONAL_JVM_ARGS)
            if (options.requireJavaArgs) add(OPTIONAL_JAVA_ARGS)
            if (options.requireWebsiteUrl) add(OPTIONAL_WEBSITE_URL)
            if (options.requireMinMemory) add(OPTIONAL_MIN_MEMORY)
            if (options.requireMaxMemory) add(OPTIONAL_MAX_MEMORY)
            if (options.requirePackRemote) add(OPTIONAL_PACK_REMOTE)
        }
    }
}

@Composable
private fun List<String>.rememberCardPosition(
    id: String,
    center: CardPosition = CardPosition.Middle,
    bottom: CardPosition = CardPosition.Bottom
): CardPosition {
    return remember(this) {
        if (this.lastOrNull() == id) bottom else center
    }
}

/**
 * 填写整合包配置信息
 * @param onFinishClick 结束编辑
 */
@Composable
fun ExportInfoScreen(
    info: ExportInfo,
    onInfoEdited: (ExportInfo) -> Unit,
    onFinishClick: () -> Unit,
    mainScreenKey: NavKey?,
    exportScreenKey: NavKey?,
) {
    BaseScreen(
        levels1 = listOf(
            Pair(NestedNavKey.VersionExport::class.java, mainScreenKey)
        ),
        Triple(NormalNavKey.VersionExports.EditInfo, exportScreenKey, false)
    ) { isVisible ->
        val isNameEmpty = remember(info) { info.name.isEmptyOrBlank() }
        val isVersionEmpty = remember(info) { info.version.isEmptyOrBlank() }
        val isAuthorEmpty = remember(info) { info.author.isEmptyOrBlank() }

        val optionalItemIDs = rememberOptionalItemIDList(info.packType.options)

        AnimatedLazyColumn(
            isVisible = isVisible,
            contentPadding = PaddingValues(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) { scope ->
            //整合包名称/版本编辑
            animatedItem(scope) { yOffset ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .offset { IntOffset(x = 0, y = yOffset.roundToPx()) },
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TextInputSettingsCard(
                        modifier = Modifier
                            .weight(0.6f)
                            .fillMaxHeight(),
                        position = CardPosition.TopStart,
                        title = stringResource(R.string.versions_export_pack_name),
                        value = info.name,
                        onValueChange = {
                            onInfoEdited(info.copy(name = it.toSingleLine()))
                        },
                        isError = isNameEmpty,
                        supportingText = if (isNameEmpty) {
                            @Composable {
                                Text(text = stringResource(R.string.generic_cannot_empty))
                            }
                        } else null,
                        singleLine = true,
                    )

                    TextInputSettingsCard(
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight(),
                        position = CardPosition.TopEnd,
                        title = stringResource(R.string.versions_export_pack_version),
                        value = info.version,
                        onValueChange = {
                            onInfoEdited(info.copy(version = it.toSingleLine()))
                        },
                        isError = isVersionEmpty,
                        supportingText = if (isVersionEmpty) {
                            @Composable {
                                Text(text = stringResource(R.string.generic_cannot_empty))
                            }
                        } else null,
                        singleLine = true,
                    )
                }
            }

            if (info.packType.options.requireAuthor) {
                animatedItem(scope) { yOffset ->
                    TextInputSettingsCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset { IntOffset(x = 0, y = yOffset.roundToPx()) },
                        position = CardPosition.Middle,
                        title = stringResource(R.string.versions_export_pack_author),
                        value = info.author,
                        onValueChange = {
                            onInfoEdited(info.copy(author = it.toSingleLine()))
                        },
                        isError = isAuthorEmpty,
                        supportingText = if (isAuthorEmpty) {
                            @Composable {
                                Text(text = stringResource(R.string.generic_cannot_empty))
                            }
                        } else null,
                        singleLine = true,
                    )
                }
            }

            animatedItem(scope) { yOffset ->
                TextInputSettingsCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(x = 0, y = yOffset.roundToPx()) },
                    position = optionalItemIDs.rememberCardPosition(OPTION_DESCRIPTION),
                    title = stringResource(R.string.versions_export_pack_summary),
                    value = info.summary ?: "",
                    onValueChange = { new ->
                        onInfoEdited(
                            info.copy(summary = new.takeIf { it.isNotEmptyOrBlank() })
                        )
                    },
                    supportingText = {
                        Text(text = stringResource(R.string.versions_export_pack_summary_hint))
                    },
                    minLines = 3,
                    singleLine = false
                )
            }

            if (info.packType.options.requireJvmArgs) {
                //游戏参数
                animatedItem(scope) { yOffset ->
                    TextInputSettingsCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset { IntOffset(x = 0, y = yOffset.roundToPx()) },
                        position = optionalItemIDs.rememberCardPosition(OPTIONAL_JVM_ARGS),
                        title = stringResource(R.string.versions_export_pack_jvm_args),
                        value = info.jvmArgs,
                        onValueChange = { new ->
                            onInfoEdited(
                                info.copy(jvmArgs = new.toSingleLine())
                            )
                        },
                        singleLine = true,
                    )
                }
            }

            if (info.packType.options.requireJavaArgs) {
                //Java虚拟机参数
                animatedItem(scope) { yOffset ->
                    TextInputSettingsCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset { IntOffset(x = 0, y = yOffset.roundToPx()) },
                        position = optionalItemIDs.rememberCardPosition(OPTIONAL_JAVA_ARGS),
                        title = stringResource(R.string.versions_export_pack_java_args),
                        value = info.javaArgs,
                        onValueChange = { new ->
                            onInfoEdited(
                                info.copy(javaArgs = new.toSingleLine())
                            )
                        },
                        singleLine = true,
                    )
                }
            }

            if (info.packType.options.requireWebsiteUrl) {
                //整合包官方网站
                animatedItem(scope) { yOffset ->
                    TextInputSettingsCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset { IntOffset(x = 0, y = yOffset.roundToPx()) },
                        position = optionalItemIDs.rememberCardPosition(OPTIONAL_WEBSITE_URL),
                        title = stringResource(R.string.versions_export_pack_website),
                        value = info.url,
                        onValueChange = { new ->
                            onInfoEdited(
                                info.copy(url = new.toSingleLine())
                            )
                        },
                        singleLine = true,
                    )
                }
            }

            if (info.packType.options.requireMinMemory) {
                //最小内存
                animatedItem(scope) { yOffset ->
                    var memory by remember(info) { mutableIntStateOf(info.minMemory) }

                    IntSliderSettingsCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset { IntOffset(x = 0, y = yOffset.roundToPx()) },
                        position = optionalItemIDs.rememberCardPosition(OPTIONAL_MIN_MEMORY),
                        title = stringResource(R.string.versions_export_pack_min_memory),
                        value = memory,
                        onValueChange = {
                            memory = it
                        },
                        onValueChangeFinished = {
                            onInfoEdited(info.copy(minMemory = memory))
                        },
                        valueRange = 0f..getMaxMemoryForSettings(LocalContext.current).toFloat(),
                        suffix = "MB"
                    )
                }
            }

            if (info.packType.options.requireMaxMemory) {
                //最大内存
                animatedItem(scope) { yOffset ->
                    var memory by remember(info) { mutableIntStateOf(info.maxMemory) }

                    IntSliderSettingsCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset { IntOffset(x = 0, y = yOffset.roundToPx()) },
                        position = optionalItemIDs.rememberCardPosition(OPTIONAL_MAX_MEMORY),
                        title = stringResource(R.string.versions_export_pack_max_memory),
                        value = memory,
                        onValueChange = {
                            memory = it
                        },
                        onValueChangeFinished = {
                            onInfoEdited(info.copy(maxMemory = memory))
                        },
                        valueRange = 0f..getMaxMemoryForSettings(LocalContext.current).toFloat(),
                        suffix = "MB"
                    )
                }
            }

            if (info.packType.options.requirePackRemote) {
                //是否打包远程资源
                animatedItem(scope) { yOffset ->
                    SwitchSettingsCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset { IntOffset(x = 0, y = yOffset.roundToPx()) },
                        position = CardPosition.Middle,
                        title = stringResource(R.string.versions_export_pack_pack_remote),
                        checked = info.packRemote,
                        onCheckedChange = {
                            onInfoEdited(info.copy(packRemote = it))
                        }
                    )
                }

                //是否打包 CurseForge 的远程资源
                animatedItem(scope) { yOffset ->
                    SwitchSettingsCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset { IntOffset(x = 0, y = yOffset.roundToPx()) },
                        position = CardPosition.Bottom,
                        title = stringResource(R.string.versions_export_pack_pack_curseforge),
                        checked = info.packCurseForge,
                        onCheckedChange = {
                            onInfoEdited(info.copy(packCurseForge = it))
                        },
                        enabled = info.packRemote
                    )
                }
            }

            //导出按钮
            animatedItem(scope) { yOffset ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(x = 0, y = yOffset.roundToPx()) }
                ) {
                    Spacer(Modifier.height(8.dp))

                    //对于打包远端资源的提示
                    AnimatedVisibility(
                        visible = info.packType.options.requirePackRemote && info.packRemote
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            WarningCard(
                                modifier = Modifier
                                    .offset { IntOffset(x = 0, y = yOffset.roundToPx()) },
                                title = stringResource(R.string.generic_tip),
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = null
                                    )
                                },
                                text = {
                                    val platformText = remember(info) {
                                        val currentPlatform = info.packType.name
                                        if (info.packCurseForge) {
                                            "$currentPlatform ${Platform.CURSEFORGE.displayName}"
                                        } else {
                                            currentPlatform
                                        }
                                    }

                                    Text(
                                        text = stringResource(R.string.versions_export_tip_remote_1, platformText),
                                        style = MaterialTheme.typography.bodySmall
                                    )

                                    Text(
                                        text = stringResource(R.string.versions_export_tip_remote_2),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            )

                            Spacer(Modifier.height(12.dp))
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = onFinishClick,
                            enabled = !isNameEmpty && !isVersionEmpty && (!info.packType.options.requireAuthor || !isAuthorEmpty)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val text = stringResource(R.string.versions_export_pack_select_files)
                                Icon(
                                    imageVector = Icons.Default.SelectAll,
                                    contentDescription = text
                                )

                                Text(text = text)
                            }
                        }
                    }
                }
            }
        }
    }
}