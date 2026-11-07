package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.CantSpotCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToPlayInterruptModifier;
import com.gempukku.swccgo.logic.modifiers.InterruptPlaysForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFlippedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Location
 * Subtype: Site
 * Title: Coruscant: ISB Central Office
 */
public class Card1240_050 extends AbstractSite {
    public Card1240_050() {
        super(Side.DARK, "Coruscant: ISB Central Office", Title.Coruscant, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setLocationLightSideGameText("Unless Blount present, Empire's Sinister Agents may not flip. Immune to Revolution.");
        setLocationDarkSideGameText("If your spy here, you may play non-[Virtual Block 3] Interrupts with 'advisor' in title for 1 Force.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.INTERIOR_SITE, Icon.PLANET);
        addImmuneToCardTitle(Title.Revolution);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        return List.of(new MayNotBeFlippedModifier(self,
                new CantSpotCondition(self, Filters.and(Filters.present(self), Filters.persona(Persona.BLOUNT))),
                Filters.Empires_Sinister_Agents));
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter yours = Filters.owner(playerOnDarkSideOfLocation);
        Condition spyHere = new OnTableCondition(self, Filters.and(yours, Filters.spy, Filters.here(self)));
        Filter advisors = Filters.and(yours, Filters.Interrupt, Filters.titleContains("advisor"), Filters.not(Icon.LEGACY_BLOCK_3));

        // Waive the Interrupt's own use-Force cost; the native extra-cost step charges the replacement amount.
        return List.of(new InterruptPlaysForFreeModifier(self, advisors, spyHere) {
            @Override
            public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
                return "Plays for 1 Force";
            }
        }, new ExtraForceCostToPlayInterruptModifier(self, advisors, spyHere, 1) {
            @Override
            public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
                return "";
            }
        });
    }
}
