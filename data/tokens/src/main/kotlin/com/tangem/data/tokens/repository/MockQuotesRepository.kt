package com.tangem.data.tokens.repository

import arrow.core.Either
import arrow.core.right
import com.tangem.data.tokens.mock.MockQuotes
import com.tangem.domain.tokens.error.TokensError
import com.tangem.domain.tokens.model.Quote
import com.tangem.domain.tokens.model.Token
import com.tangem.domain.tokens.repository.QuotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class MockQuotesRepository : QuotesRepository {

    override fun getQuotes(tokensIds: Set<Token.ID>, refresh: Boolean): Flow<Either<TokensError, Set<Quote>>> {
        return flowOf(
            MockQuotes.quotes
                .filter { it.tokenId in tokensIds }
                .toSet()
                .right(),
        )
    }
}