package com.gempukku.swccgo.cards.set1112.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseForceResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Effect
 * Title: Forced Servitude
 */
public class Card1112_032 extends AbstractNormalEffect {
    public Card1112_032() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Forced_Servitude, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setLore("The Empire often uses droids for nefarious purposes. Imperials compel droids to do jobs that are repugnant to humans. An automaton has no ethical conscience.");
        setGameText("Deploy on table. Your troopers are Heroic. Opponent's total weapon destiny targeting your Heroic cards is +2. When opponent is about to remove their last card from Life Force, you may first place all your troopers in Used Pile (last card must still be removed). (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, Filters.and(Filters.your(self), Filters.trooper), Keyword.HEROIC));
        modifiers.add(new TotalWeaponDestinyModifier(self, Filters.any, 2, Filters.and(Filters.your(self), Keyword.HEROIC)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        if (TriggerConditions.isAboutToLoseForce(game, effectResult, opponent)) {
            AboutToLoseForceResult result = (AboutToLoseForceResult) effectResult;
            if (game.getGameState().getPlayerLifeForce(opponent) <= result.getForceLossAmount(game)
                    && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.trooper))) {
                Collection<PhysicalCard> troopers = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.trooper));
                if (!troopers.isEmpty()) {
                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Place troopers in Used Pile");
                    action.appendEffect(
                            new PlaceCardsInUsedPileFromTableEffect(action, troopers));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}
