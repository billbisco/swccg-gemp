package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 6.1: Reflections IV
 * Type: Defensive Shield
 * Title: Another Pathetic Lifeform (V)
 * Appended leftover id 1261_117 (Reflections III host). 1261_118 is the Coruscant alias. Blanked by 30 Apr 2013.
 */
public class Card1261_117 extends AbstractDefensiveShield {
    public Card1261_117() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Another Pathetic Lifeform", ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Young Obi-Wan has much to learn about the living Force. Patience with others is also high on that list.");
        setGameText("Plays on table. M'iiyoom Onith's game text is canceled. Any docking bay Brangus exchanges with a card in Lost Pile is placed out of play. While opponent has a non-unique alien (or starfighter), or Dark Jedi in battle, opponent may not draw more than two battle destiny.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextModifier(self, Filters.Miiyoom_Onith));
        Condition ignoresAliens = new GameTextModificationCondition(self, ModifyGameTextType.LEGACY__REF_III_ANOTHER_PATHETIC_LIFEFORM__IGNORES_YOUR_NONUNIQUE_ALIENS);
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, new OrCondition(
                new AndCondition(new NotCondition(ignoresAliens), new InBattleCondition(self, Filters.and(Filters.opponents(self), Filters.non_unique, Filters.or(Filters.alien, Filters.starfighter)))),
                new InBattleCondition(self, Filters.and(Filters.opponents(self), Filters.Dark_Jedi))), 2, opponent));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (effectResult.getType() != EffectResult.Type.EXCHANGE_WITH_CARD_PILE) {
            return null;
        }
        String exchangingPlayer = effectResult.getPerformingPlayerId();
        if (exchangingPlayer == null
                || !GameConditions.canSpot(game, self, Filters.and(Filters.Brangus_Glee, Filters.owner(exchangingPlayer)))) {
            return null;
        }
        PhysicalCard top = game.getGameState().getTopOfCardPile(exchangingPlayer, Zone.LOST_PILE);
        if (top == null || !Filters.docking_bay.accepts(game, top)) {
            return null;
        }
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setText("Place exchanged docking bay out of play");
        action.appendEffect(new PlaceCardOutOfPlayFromOffTableEffect(action, top));
        return Collections.singletonList(action);
    }
}
