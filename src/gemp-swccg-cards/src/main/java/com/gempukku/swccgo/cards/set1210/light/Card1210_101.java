package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.TakeFirstBattleWeaponsSegmentActionEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Rebel
 * Title: Han Solo
 */
public class Card1210_101 extends AbstractRebel {
    public Card1210_101() {
        super(Side.LIGHT, 1, 3, 4, 3, 6, Title.Han_Solo, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Smuggler, gambler and 'freelance law-bender'. Crafty Corellian pirate. Rebel hero. Owns Millennium Falcon. Co-pilot Chewbacca promised him 'life-debt'. Has bounty on head.");
        setGameText("Adds 3 to power of anything he pilots. Cards with 'Han' in title are destiny +3. May use 1 Force to cancel Boba Fett or Jabba's game text for remainder of turn. If opponent just initiated battle here, may use 1 Force to take the first weapons segment action.");
        addPersona(Persona.HAN);
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.SMUGGLER, Keyword.GAMBLER, Keyword.PIRATE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new DestinyModifier(self, Filters.titleContains("Han"), 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__HAN_SOLO_V__CANCEL_GAME_TEXT;
        Filter targetFilter = Filters.and(Filters.or(Filters.Boba_Fett, Filters.Jabba), Filters.canBeTargetedBy(self));
        if (GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTarget(game, self, targetFilter)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel Boba Fett or Jabba's game text");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Boba Fett or Jabba", targetFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s game text",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.appendEffect(
                                                    new CancelGameTextUntilEndOfTurnEffect(action, targetedCard));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__HAN_SOLO_V__FIRST_WEAPONS;
        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.here(self))
                && GameConditions.canUseForce(game, playerId, 1)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take first weapons segment action");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.allowResponses("Take first weapons segment action",
                    new UnrespondableEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new TakeFirstBattleWeaponsSegmentActionEffect(action, playerId));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
