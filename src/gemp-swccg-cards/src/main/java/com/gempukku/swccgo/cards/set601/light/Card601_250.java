package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToPowerModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 6
 * Type: Starship
 * Subtype: Starfighter
 * Title: Overseer
 * Text: PC-E3, the Virtual Cards Master List (2013-04-27; the PC card image, PC-E2, came first), which repeats the 2009 release-sheet words ("If a Dark Jedi or
 * Sith was just deployed"); the PC website card-list text (PC-E1, 2012) is version 2261_1106 (Bill 2026-11-11).
 */
public class Card601_250 extends AbstractStarfighter {
    public Card601_250() {
        super(Side.LIGHT, 3, 2, 3, null, 4, 3, 5, "Overseer", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setGameText("May add Harc as a pilot. During battle, adds one destiny to total power. If a Dark Jedi or Sith was just deployed to a related site, may ▼ a related location. Immune to attrition < 4 (< 6 at Bespin).");
        addIcons(Icon.PREMIUM, Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_6);
        addModelType(ModelType.Z_95_HEADHUNTER);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Harc);
        setAsLegacy(true);
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.Harc;
    }

    /** Master List (PC-E3) and the 2009 sheet: "If a Dark Jedi or Sith was just deployed" (by either player; the card list, 2261_1106, says "If opponent just deployed"). */
    protected Filter justDeployedFilter(PhysicalCard self) {
        return Filters.or(Filters.Dark_Jedi, Filters.Sith);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OVERSEER__DOWNLOAD_RELATED_LOCATION;

        // Check condition(s)
        if (TriggerConditions.justDeployedTo(game, effectResult, justDeployedFilter(self), Filters.relatedSite(self))
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, true, false)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Deploy related location from Reserve Deck");
            action.setActionMsg("Deploy a related location from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.location, Filters.relatedLocation(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsDestinyToPowerModifier(self, new InBattleCondition(self), 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(4, 6, new AtCondition(self, Title.Bespin))));
        return modifiers;
    }
}
