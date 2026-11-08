package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_034;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.SiteAndCardsThereToUsedPileWhenLeavesTableModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.time.LocalDate;
import java.util.List;

/**
 * Set: Virtual Block 6.1
 * Type: Effect
 * Title: Fleet Security Protocols
 * PC-E1 version of 1261_34: the revised Virtual Block 6.1 sheet text (2010-04-20), reprinted by the Virtual Cards
 * Master List: your starship (or vehicle) in battle is power +2 for each of your characters at its related sites, and
 * only non-unique starship (or vehicle) sites and your cards there go to Used Pile. The PC card-list rewrite (PC-E2,
 * by 2012-07-21) is the current card. Code and tests restored from GEMP before PR #732 (commit e34136d63) made
 * this text the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1034 extends Card1261_034 {
    public Card2261_1034() {
        setGameText("Deploy on table. During battle involving your starship (or vehicle), it is power +2 for each of your characters at its related sites (they may be forfeited to reduce battle damage and attrition). Once per turn, may deploy a non-unique starship (or vehicle) site from Reserve Deck; reshuffle (if such sites leave table, place them and all your cards there in Used Pile). (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1261_34", "PC-E1",
                "Virtual Block 6.1 release sheet, revised (2010-04-20), card #34", LocalDate.of(2010, 4, 20), LocalDate.of(2012, 7, 21)));
    }

    private static final Filter DOWNLOADABLE_SITE = Filters.and(Filters.non_unique, Filters.site,
            Filters.or(Filters.starship_site, Filters.vehicle_site));

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter yourVesselsInBattle = Filters.and(Filters.your(self), Filters.or(Filters.starship, Filters.vehicle), Filters.participatingInBattle);
        return List.of(
                new PowerModifier(self, yourVesselsInBattle,
                        new BaseEvaluator() {
                            @Override
                            public float evaluateExpression(GameState state, ModifiersQuerying modifiers, PhysicalCard vessel) {
                                if (!state.isDuringBattle())
                                    return 0;
                                return 2 * Filters.countActive(state.getGame(), self, Filters.and(Filters.your(self),
                                        Filters.character, Filters.at(Filters.siteOfStarshipOrVehicle(vessel))));
                            }
                        }),
                new SiteAndCardsThereToUsedPileWhenLeavesTableModifier(self, DOWNLOADABLE_SITE, null, self.getOwner()));
    }
}
