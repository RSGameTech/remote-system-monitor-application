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
    title: String = "Add Device",
    confirmText: String = "Add",
    initialIconType: IconType = IconType.LAPTOP,
    initialLabel: String = "",
    initialAddress: String = "",
    initialApiKey: String = "",
    onDismiss: () -> Unit,
    onConfirm: (IconType, String, String, String) -> Unit
) {
    var selectedIcon by remember { mutableStateOf(initialIconType) }
    var label by remember { mutableStateOf(initialLabel) }
    var address by remember { mutableStateOf(initialAddress) }
    var apiKey by remember { mutableStateOf(initialApiKey) }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(title, style = MaterialTheme.typography.headlineSmall)

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
                label = { Text("Name") },
                placeholder = { Text("My Server") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                placeholder = { Text("192.168.1.100:8080") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("API Key") },
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
                    onClick = { onConfirm(selectedIcon, label, address, apiKey) },
                    enabled = label.isNotBlank() && address.isNotBlank()
                ) {
                    Text(confirmText)
                }
            }
        }
    }
}
