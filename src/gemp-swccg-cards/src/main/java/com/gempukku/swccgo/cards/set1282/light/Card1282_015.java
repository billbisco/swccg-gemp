package com.gempukku.swccgo.cards.set1282.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.LegacyCancelActions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyForWeaponFiredByModifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.2
 * Type: Character
 * Subtype: Rebel
 * Title: Captain Verrack (V)
 */
public class Card1282_015 extends AbstractRebel {
    public Card1282_015() {
        super(Side.LIGHT, 3, 2, 2, 2, 4, "Captain Verrack", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Mon Calamari computer technician. Expert in power systems. His skills are desperately needed by Rebel fleet. Gunner.");
        setGameText("Adds 1 to power of anything he pilots. While aboard your starship, its total weapon destiny is +1. Whenever you hit a starship here, opponent loses 1 Force. During battle, may lose 1 Force to cancel [Coruscant] Watch Your Back!.");
        addIcons(Icon.PILOT);
        addKeywords(Keyword.GUNNER, Keyword.CAPTAIN);
        setSpecies(Species.MON_CALAMARI);
    }

    private static Filter coruscantWatchYourBack() {
        return Filters.and(Filters.Watch_Your_Back, Icon.CORUSCANT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1));
        modifiers.add(new TotalWeaponDestinyForWeaponFiredByModifier(self,
                Filters.and(Filters.your(self), Filters.starship, Filters.hasAboard(self)), 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justHitBy(game, effectResult, Filters.and(Filters.starship, Filters.atSameLocation(self)), Filters.your(self))) {
            String opponent = game.getOpponent(self.getOwner());
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make opponent lose 1 Force");
            action.setActionMsg("Make opponent lose 1 Force");
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.isDuringBattle(game)
                && TriggerConditions.isPlayingCard(game, effect, coruscantWatchYourBack())
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            LegacyCancelActions.buildLoseForceToCancelCardBeingPlayedAction(action, effect, 1);
            return Collections.singletonList(action);
        }
        return null;
    }
}
