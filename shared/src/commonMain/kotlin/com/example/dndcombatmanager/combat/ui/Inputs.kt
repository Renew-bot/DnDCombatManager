package com.example.dndcombatmanager.combat.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dndcombatmanager.combat.theme.Fonts
import com.example.dndcombatmanager.combat.theme.oklch

val InputBorder = oklch(0.34f, 0.02f, 55f)
val InputBorderSubtle = oklch(0.32f, 0.02f, 55f)
val InputText = oklch(0.90f, 0.02f, 80f)
val LabelColor = oklch(0.58f, 0.02f, 70f)

@Composable
fun FieldLabel(text: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
            text = text.uppercase(),
            color = LabelColor,
            fontFamily = Fonts.body,
            fontSize = 11.sp,
            letterSpacing = 0.6.sp,
        )
        content()
    }
}

@Composable
fun DarkTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    textAlign: TextAlign = TextAlign.Start,
    background: Color = oklch(0.16f, 0.02f, 55f),
    singleLine: Boolean = true,
    minLines: Int = 1,
) {
    Box(
        modifier = modifier
            .background(background, RoundedCornerShape(7.dp))
            .border(BorderStroke(1.dp, InputBorder), RoundedCornerShape(7.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart,
    ) {
        if (value.isEmpty() && placeholder.isNotEmpty()) {
            Text(placeholder, color = oklch(0.5f, 0.02f, 70f), fontFamily = Fonts.body, fontSize = 14.sp)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            minLines = minLines,
            textStyle = TextStyle(color = InputText, fontFamily = Fonts.body, fontSize = 14.sp, textAlign = textAlign),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(InputText),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun DarkNumberField(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    background: Color = oklch(0.16f, 0.02f, 55f),
    textColor: Color = InputText,
) {
    var text by remember(value) { mutableStateOf(value.toString()) }
    Box(
        modifier = modifier
            .background(background, RoundedCornerShape(7.dp))
            .border(BorderStroke(1.dp, InputBorder), RoundedCornerShape(7.dp))
            .padding(horizontal = 8.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        BasicTextField(
            value = text,
            onValueChange = { newText ->
                text = newText
                val parsed = newText.toIntOrNull()
                onValueChange(parsed ?: 0)
            },
            singleLine = true,
            textStyle = TextStyle(color = textColor, fontFamily = Fonts.mono, fontSize = 14.sp, textAlign = textAlign),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(textColor),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** Like [DarkNumberField] but a blank field means "unset" (null) instead of coercing to 0. */
@Composable
fun DarkNullableNumberField(
    value: Int?,
    onValueChange: (Int?) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    textAlign: TextAlign = TextAlign.Start,
    background: Color = oklch(0.16f, 0.02f, 55f),
    textColor: Color = InputText,
) {
    var text by remember(value) { mutableStateOf(value?.toString() ?: "") }
    Box(
        modifier = modifier
            .background(background, RoundedCornerShape(7.dp))
            .border(BorderStroke(1.dp, InputBorder), RoundedCornerShape(7.dp))
            .padding(horizontal = 8.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (text.isEmpty() && placeholder.isNotEmpty()) {
            Text(
                placeholder, color = textColor.copy(alpha = 0.4f), fontFamily = Fonts.mono, fontSize = 14.sp, textAlign = textAlign,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        BasicTextField(
            value = text,
            onValueChange = { newText ->
                text = newText
                onValueChange(if (newText.isBlank()) null else newText.toIntOrNull())
            },
            singleLine = true,
            textStyle = TextStyle(color = textColor, fontFamily = Fonts.mono, fontSize = 14.sp, textAlign = textAlign),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(textColor),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun DarkTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .background(oklch(0.19f, 0.02f, 55f), RoundedCornerShape(8.dp))
            .border(BorderStroke(1.dp, InputBorderSubtle), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 9.dp),
    ) {
        if (value.isEmpty() && placeholder.isNotEmpty()) {
            Text(placeholder, color = oklch(0.5f, 0.02f, 70f), fontFamily = Fonts.body, fontSize = 13.sp)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(color = oklch(0.88f, 0.02f, 80f), fontFamily = Fonts.body, fontSize = 13.sp),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(InputText),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

data class SelectOption<T>(val value: T, val label: String)

@Composable
fun <T> DarkSelectField(
    selected: T,
    options: List<SelectOption<T>>,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    background: Color = oklch(0.16f, 0.02f, 55f),
) {
    var expanded by remember { mutableStateOf(false) }
    val currentLabel = options.find { it.value == selected }?.label ?: ""
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(background, RoundedCornerShape(7.dp))
                .border(BorderStroke(1.dp, InputBorder), RoundedCornerShape(7.dp))
                .clickable { expanded = true }
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(currentLabel, color = InputText, fontFamily = Fonts.body, fontSize = 14.sp)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = LabelColor, modifier = Modifier.size(16.dp))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(text = { Text(opt.label) }, onClick = {
                    onSelect(opt.value)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color,
    background: Color,
    borderColor: Color? = null,
    fontWeight: FontWeight = FontWeight.SemiBold,
    fontSize: androidx.compose.ui.unit.TextUnit = 13.sp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 15.dp, vertical = 9.dp),
    enabled: Boolean = true,
    shape: RoundedCornerShape = RoundedCornerShape(9.dp),
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
) {
    Box(
        modifier = modifier
            .background(background, shape)
            .let { if (borderColor != null) it.border(BorderStroke(1.dp, borderColor), shape) else it }
            .clickable(enabled = enabled) { onClick() }
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        if (leadingIcon == null && trailingIcon == null) {
            Text(text, color = textColor, fontFamily = Fonts.body, fontWeight = fontWeight, fontSize = fontSize, textAlign = TextAlign.Center)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                leadingIcon?.let { Icon(it, contentDescription = null, tint = textColor, modifier = Modifier.size(14.dp)) }
                Text(text, color = textColor, fontFamily = Fonts.body, fontWeight = fontWeight, fontSize = fontSize, textAlign = TextAlign.Center)
                trailingIcon?.let { Icon(it, contentDescription = null, tint = textColor, modifier = Modifier.size(14.dp)) }
            }
        }
    }
}

/** The gold gradient CTA used for "Tour suivant", "Ajouter", "Enregistrer", etc. */
@Composable
fun GradientPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit = 13.5.sp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
    shape: RoundedCornerShape = RoundedCornerShape(9.dp),
    trailingIcon: ImageVector? = null,
) {
    val textColor = oklch(0.16f, 0.02f, 60f)
    Box(
        modifier = modifier
            .background(
                androidx.compose.ui.graphics.Brush.linearGradient(listOf(oklch(0.76f, 0.15f, 72f), oklch(0.65f, 0.16f, 55f))),
                shape,
            )
            .clickable { onClick() }
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        if (trailingIcon == null) {
            Text(text, color = textColor, fontFamily = Fonts.body, fontWeight = FontWeight.Bold, fontSize = fontSize, textAlign = TextAlign.Center)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text, color = textColor, fontFamily = Fonts.body, fontWeight = FontWeight.Bold, fontSize = fontSize, textAlign = TextAlign.Center)
                Icon(trailingIcon, contentDescription = null, tint = textColor, modifier = Modifier.size(14.dp))
            }
        }
    }
}
