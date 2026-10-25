package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Ability, Ability, Ability
 */
public class Card1220_024 extends AbstractNormalEffect {
    public Card1220_024() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Ability, Ability, Ability", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Ben. . . Ben, please! Ben. . . Leia! Hear me, Leia!'");
        setGameText("Deploy on table. You may not play Imperial Barrier. Senators are deploy +2. Whenever you initiate a battle, may retrieve your topmost non-[Permanent Weapon], non-[Maintenance] character. May place the top card of your Reserve Deck in Lost Pile to place a combat card in owner's Lost Pile. (Immune to Alter.)");
        addIcons(Icon.CLOUD_CITY);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotPlayModifier(self, Filters.Imperial_Barrier, playerId));
        modifiers.add(new DeployCostModifier(self, Filters.senator, 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter topmostCharacter = Filters.and(Filters.character, Filters.not(Icon.PERMANENT_WEAPON), Filters.not(Icon.MAINTENANCE));
        if (TriggerConditions.battleInitiated(game, effectResult, playerId)
                && GameConditions.hasLostPile(game, playerId)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve topmost character");
            action.appendEffect(
                    new RetrieveCardEffect(action, playerId, true, topmostCharacter));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ABILITY_ABILITY_ABILITY_V__PLACE_COMBAT_CARD;
        if (GameConditions.hasReserveDeck(game, playerId)
                && Filters.canSpotFromStacked(game, Filters.combatCard)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Place a combat card in Lost Pile");
            action.appendCost(
                    new LoseTopCardOfReserveDeckEffect(action, playerId));
            action.appendEffect(
                    new ChooseStackedCardEffect(action, playerId, Filters.any, Filters.combatCard) {
                        @Override
                        protected void cardSelected(PhysicalCard combatCard) {
                            action.appendEffect(
                                    new PutStackedCardInLostPileEffect(action, combatCard.getOwner(), combatCard, false));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
