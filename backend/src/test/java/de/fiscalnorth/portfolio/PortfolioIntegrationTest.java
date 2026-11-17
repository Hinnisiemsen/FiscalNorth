package de.fiscalnorth.portfolio;

import de.fiscalnorth.config.H2IdentityAligner;
import de.fiscalnorth.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PortfolioIntegrationTest extends IntegrationTestBase {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private H2IdentityAligner h2IdentityAligner;

    @Test
    void addHolding_afterExplicitSeedId_succeedsForEtf() throws Exception {
        RequestPostProcessor user = authenticatedUser();

        mockMvc.perform(get("/api/portfolio").with(user)).andExpect(status().isOk());

        Long portfolioId = jdbcTemplate.queryForObject(
                "SELECT id FROM portfolio ORDER BY id DESC LIMIT 1", Long.class);
        jdbcTemplate.update(
                """
                INSERT INTO holding (id, portfolio_id, symbol, name, quantity, cost_basis, asset_class)
                VALUES (1, ?, 'SEED', 'Seed Co', 1, 10, 'STOCK')
                """,
                portfolioId);
        h2IdentityAligner.align();

        mockMvc.perform(post("/api/portfolio/holdings")
                        .with(user)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "symbol": "VWCE",
                                  "name": "FTSE All-World",
                                  "quantity": 2,
                                  "costBasis": 180,
                                  "assetClass": "ETF"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.holdings[*].symbol", hasItem("VWCE")))
                .andExpect(jsonPath("$.holdings[?(@.symbol=='VWCE')].assetClass", hasItem("ETF")));
    }

    @Test
    void getPortfolio_returnsOverview() throws Exception {
        mockMvc.perform(get("/api/portfolio").with(authenticatedUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.holdings.length()", is(0)));
    }
}
