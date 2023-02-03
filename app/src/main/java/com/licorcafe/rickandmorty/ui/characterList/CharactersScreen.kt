package com.licorcafe.rickandmorty.ui.characterList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.licorcafe.rickandmorty.R
import com.licorcafe.rickandmorty.common.ExtendedTheme
import com.licorcafe.rickandmorty.common.RickAndMortyLoading
import com.licorcafe.rickandmorty.common.RickAndMortyProblem
import com.licorcafe.rickandmorty.domain.model.CharacterId
import com.licorcafe.rickandmorty.domain.model.LocationUrl
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow

@Composable
fun CharactersScreen(
    stateFlow: Flow<CharactersViewState>,
    initialValue: CharactersViewState,
    actions: Channel<CharacterAction>
) {
    val state by stateFlow.collectAsState(initialValue)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                backgroundColor = ExtendedTheme.colors.primary
            )
        },

    ) { paddingValues ->
        Box(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
            when (val s = state) {
                Loading -> RickAndMortyLoading()
                is Content -> Content(s) { it, it2 ->
                    actions.trySend(LoadDetails(it, it2))
                }
                is Problem -> RickAndMortyProblem(s.stringId) { actions.trySend(Refresh) }
            }
        }
    }
}

@Composable
private fun Content(content: Content, loadDetails: (Long, LocationUrl) -> Unit) {
    Column(Modifier.testTag("CharactersContent")) {
        LazyVerticalGrid(GridCells.Adaptive(175.dp), Modifier.weight(1f)) {
            items(content.characters) {
                CharacterItem(it, loadDetails)
            }
        }
    }
}

@Composable
fun CharacterItem(entity: CharactersViewEntity, onClick: (CharacterId, LocationUrl) -> Unit) {
    Box(
        modifier = Modifier
            .clickable { onClick(entity.id, entity.locationUrl) }
            .aspectRatio(1f)
            .semantics(mergeDescendants = true) {},
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(entity.imageUrl)
                .crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.White.copy(alpha = 0.7f)),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = entity.name,
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}
