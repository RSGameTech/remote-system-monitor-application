package `in`.rsgametech.systemmonitor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import `in`.rsgametech.systemmonitor.model.IconType

@Composable
fun AddItemForm(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onConfirm: (IconType, String, String) -> Unit
) {
    var selectedIcon by remember { mutableStateOf(IconType.LAPTOP) }
    var label by remember { mutableStateOf("") }
    var supportingText by remember { mutableStateOf("") }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Add Device", style = MaterialTheme.typography.headlineSmall)

            Text("Select Icon", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconType.entries.forEach { iconType ->
                    if (selectedIcon == iconType) {
                        FilledTonalIconButton(onClick = {}) {
                            Icon(iconType.icon, contentDescription = iconType.displayName)
                        }
                    } else {
                        IconButton(onClick = { selectedIcon = iconType }) {
                            Icon(iconType.icon, contentDescription = iconType.displayName)
                        }
                    }
                }
            }

            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("Label") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = supportingText,
                onValueChange = { supportingText = it },
                label = { Text("Supporting Text") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                TextButton(
                    onClick = { onConfirm(selectedIcon, label, supportingText) },
                    enabled = label.isNotBlank()
                ) {
                    Text("Add")
                }
            }
        }
    }
}
