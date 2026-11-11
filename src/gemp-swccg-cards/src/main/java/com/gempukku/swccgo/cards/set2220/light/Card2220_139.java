package com.gempukku.swccgo.cards.set2220.light;

import java.time.LocalDate;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardsEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Away Put Your Weapon
 * PC-E2 version of 1220_139 (Virtual Block 2 release sheet, official text 2009-12-07): "up to five weapons (may take one retrieved card into hand)"; same play.
 * Batch S1 (Bill 2026-11-11, H1): the September 2009 first printing is lost; the PC card image wording (slip 140E, PC-E1, 2220_1139)
 * is the 9 September 2009 errata, this sheet text is PC-E2 and the PC card list text (the current card) is PC-E3.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_139 extends AbstractUsedOrLostInterrupt {
    public Card2220_139() {
        super(Side.LIGHT, 5, Title.Away_Put_Your_Weapon, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("'I mean you no harm.'");
        setGameText("USED: An opponent's just drawn [Permanent Weapon] weapon destiny is -3. (Immune to Sense.) LOST: Once per game, retrieve up to five weapons (may take one retrieved card into hand).");
        setLegacyVersion(new LegacyVersion("1220_139", "PC-E2",
                "Virtual Block 2 release sheet (2009-12-07), card #139E; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        if (TriggerConditions.isWeaponDestinyJustDrawnBy(game, effectResult, opponent, Icon.PERMANENT_WEAPON)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Subtract 3 from weapon destiny");
            action.setImmuneTo(Title.Sense);
            action.allowResponses("Make opponent's permanent weapon destiny -3",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new ModifyDestinyEffect(action, -3));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId retrieveId = GameTextActionId.LEGACY__AWAY_PUT_YOUR_WEAPON_V__RETRIEVE_WEAPONS;
        if (GameConditions.isOncePerGame(game, self, retrieveId)
                && GameConditions.hasLostPile(game, playerId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, retrieveId, CardSubtype.LOST);
            action.setText("Retrieve up to 5 weapons");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.allowResponses("Retrieve up to 5 weapons",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new RetrieveCardsEffect(action, playerId, 5, true, Filters.weapon) {
                                        @Override
                                        public boolean mayBeTakenIntoHand() {
                                            return true;
                                        }
                                    });
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
