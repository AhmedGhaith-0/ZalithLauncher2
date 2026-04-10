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

package com.movtery.zalithlauncher.utils.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import com.movtery.zalithlauncher.utils.logging.Logger.lWarning
import java.io.File

/**
 * 将 [Drawable] 转换为 [Bitmap]
 * 如果该 Drawable 已经是 [BitmapDrawable] 且其内部 Bitmap 不为 null，则直接返回该 Bitmap
 * 否则渲染到一个新的 Bitmap 上
 */
fun Drawable.toBitmap(): Bitmap {
    if (this is BitmapDrawable && this.bitmap != null) {
        return this.bitmap
    }

    val width = if (intrinsicWidth > 0) intrinsicWidth else 1
    val height = if (intrinsicHeight > 0) intrinsicHeight else 1

    val bitmap = createBitmap(width, height)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)

    return bitmap
}

/**
 * 尝试判断文件是否为一个图片
 */
fun File.isImageFile(): Boolean {
    if (!this.exists()) return false

    return try {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(this.absolutePath, options)
        options.outWidth > 0 && options.outHeight > 0
    } catch (e: Exception) {
        lWarning("An exception occurred while trying to determine if ${this.absolutePath} is an image.", e)
        false
    }
}

/**
 * 获取指定坐标的像素颜色(0-based)
 * @param x X坐标
 * @param y Y坐标
 * @return 该坐标的颜色对象，如果坐标越界则返回`null`
 */
fun Bitmap.getColorByPos(x: Int, y: Int): Color? {
    if (x !in 0..< width || y !in 0..< height) return null
    val pixel = this[x, y]
    return Color.valueOf(pixel)
}

/**
 * 判断指定坐标的颜色是否符合要求(0-based)
 * @param x X坐标
 * @param y Y坐标
 * @param predicate 判断条件
 */
fun Bitmap.isColorMatch(x: Int, y: Int, predicate: (Color) -> Boolean): Boolean {
    val color = getColorByPos(x, y) ?: return false
    return predicate(color)
}

/**
 * 判断指定区域内的颜色是否符合要求(0-based)
 * @param xRange X坐标范围
 * @param yRange Y坐标范围
 * @param predicate 判断条件，参数：颜色, x坐标, y坐标
 */
fun Bitmap.isColorMatch(
    xRange: IntRange,
    yRange: IntRange,
    predicate: (Color, x: Int, y: Int) -> Boolean,
    requireAll: Boolean = true
): Boolean {
    for (x in xRange) {
        for (y in yRange) {
            val match = isColorMatch(x, y) { color ->
                predicate(color, x, y)
            }
            if (requireAll && !match) return false
            if (!requireAll && match) return true
        }
    }

    return requireAll
}