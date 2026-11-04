package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfForcePileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.MayFireOneWeaponTwicePerBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Character
 * Subtype: Alien
 * Title: Bane Malar, Spice Addict
 */
public class Card1281_002 extends AbstractAlien {
    public Card1281_002() {
        super(Side.DARK, 2, 4, 2, 4, 4, "Bane Malar, Spice Addict", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setLore("Black Sun agent, assassin and bounty hunter.");
        setGameText("Adds 2 to the power of anything he pilots. Once per turn, may 'mindscan' by placing a card from hand on your Used Pile; choose: Until end of turn Malar is power +2 and may fire a weapon twice per battle. OR Peek at the top X cards of your Force Pile where X = number of characters here.");
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.BLACK_SUN_AGENT, Keyword.ASSASSIN, Keyword.BOUNTY_HUNTER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__BANE_MALAR_SPICE_ADDICT__MINDSCAN;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasHand(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("'Mindscan'");
            action.setActionMsg("'Mindscan' by placing a card from hand on Used Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new PutCardFromHandOnUsedPileEffect(action, playerId));
            // Perform result(s)
            action.appendEffect(
                    new PlayoutDecisionEffect(action, playerId,
                            new MultipleChoiceAwaitingDecision("Choose 'mindscan' result", new String[]{"Power +2 and may fire a weapon twice per battle", "Peek at top X cards of Force Pile"}) {
                                @Override
                                protected void validDecisionMade(int index, String result) {
                                    if (index == 0) {
                                        action.appendEffect(
                                                new AddUntilEndOfTurnModifierEffect(action,
                                                        new PowerModifier(self, self, 2), GameUtils.getCardLink(self) + " is power +2"));
                                        action.appendEffect(
                                                new AddUntilEndOfTurnModifierEffect(action,
                                                        new MayFireOneWeaponTwicePerBattleModifier(self, null, Filters.weapon), GameUtils.getCardLink(self) + " may fire a weapon twice per battle"));
                                    }
                                    else {
                                        int numCharactersHere = Filters.countActive(game, self, Filters.and(Filters.character, Filters.here(self)));
                                        if (numCharactersHere > 0) {
                                            action.appendEffect(
                                                    new PeekAtTopCardsOfForcePileEffect(action, playerId, numCharactersHere));
                                        }
                                    }
                                }
                            }));
            return Collections.singletonList(action);
        }
        return null;
    }
}
