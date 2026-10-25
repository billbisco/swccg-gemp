package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForLightsaberCombatDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Release Your Anger
 */
public class Card1220_064 extends AbstractUsedOrLostInterrupt {
    public Card1220_064() {
        super(Side.DARK, 4, Title.Release_Your_Anger, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Only your hatred can destroy me.'");
        setGameText("Destiny +2 when drawn for weapon or lightsaber combat destiny. USED: Take Your Powers Are Weak, Old Man into hand from Reserve Deck; reshuffle. LOST: If you just lost a character armed with a non-[Permanent Weapon] lightsaber, lose 1 Force to take that character (or lightsaber) into hand.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DestinyWhenDrawnForWeaponDestinyModifier(self, 2));
        modifiers.add(new DestinyWhenDrawnForLightsaberCombatDestinyModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__RELEASE_YOUR_ANGER_V__TAKE_YOUR_POWERS;
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take Your Powers Are Weak, Old Man into hand");
            action.allowResponses("Take Your Powers Are Weak, Old Man into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.title(Title.Your_Powers_Are_Weak_Old_Man), true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        Filter nonPermanentLightsaber = Filters.and(Filters.lightsaber, Filters.not(Icon.PERMANENT_WEAPON));
        if (TriggerConditions.justLostFromAttachedTo(game, effectResult, nonPermanentLightsaber, Filters.and(Filters.your(self), Filters.character))
                && GameConditions.canUseForce(game, playerId, 1)) {
            final PhysicalCard saber = ((LostFromTableResult) effectResult).getCard();
            final PhysicalCard character = ((LostFromTableResult) effectResult).getFromAttachedTo();
            Filter takeFilter = Filters.or(saber, character);
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Take character or lightsaber into hand");
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            action.allowResponses("Take " + GameUtils.getCardLink(character) + " or " + GameUtils.getCardLink(saber) + " into hand",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new TakeCardIntoHandFromLostPileEffect(action, playerId, takeFilter, false));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
