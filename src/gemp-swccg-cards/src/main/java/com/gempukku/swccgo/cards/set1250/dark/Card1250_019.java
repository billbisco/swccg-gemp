package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CaptureWithSeizureEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 5
 * Type: Effect
 * Title: Nute Gunray's Bounty
 */
public class Card1250_019 extends AbstractNormalEffect {
    public Card1250_019() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Nute Gunray's Bounty", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setLore("Palpatine suggested to the Queen that she acquiesce to the Trade Federation, just for the time being");
        setGameText("Deploy on Amidala or opponent's senator. During your move phase, unless a Jedi here, your alien bounty hunter present may capture and sieze that character. During your control phase, if that character is escorted by an alien, opponent loses 1 Force.");
        addIcons(Icon.EPISODE_I, Icon.CORUSCANT);
    }

    @Override
    public boolean isActiveWhileAttachedToCaptive() {
        // The control phase clause works on 'that character' while it is escorted.
        return true;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.persona(Persona.AMIDALA), Filters.and(Filters.opponents(self), Filters.senator));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        final PhysicalCard character = self.getAttachedTo();
        if (character == null) {
            return null;
        }

        // Move phase: capture and seize
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE)
                && !Filters.captive.accepts(game, character)
                && !GameConditions.canSpot(game, self, Filters.and(Filters.Jedi, Filters.here(self)))
                && Filters.canBeTargetedBy(self, TargetingReason.TO_BE_CAPTURED).accepts(game, character)) {
            final Filter bountyHunterFilter = Filters.and(Filters.your(self), Filters.alien, Filters.bounty_hunter,
                    Filters.presentWith(character), Filters.canEscortCaptive(character));
            if (GameConditions.canSpot(game, self, bountyHunterFilter)) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, GameTextActionId.LEGACY__NUTE_GUNRAYS_BOUNTY__CAPTURE);
                action.setText("Capture and seize " + GameUtils.getFullName(character));
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose bounty hunter to seize " + GameUtils.getCardLink(character), bountyHunterFilter) {
                            @Override
                            protected void cardSelected(final PhysicalCard bountyHunter) {
                                action.addAnimationGroup(bountyHunter, character);
                                action.setActionMsg("Have " + GameUtils.getCardLink(bountyHunter) + " capture and seize " + GameUtils.getCardLink(character));
                                action.appendEffect(new CaptureWithSeizureEffect(action, character, bountyHunter));
                            }
                        });
                actions.add(action);
            }
        }

        // Control phase: Force loss
        GameTextActionId lossActionId = GameTextActionId.LEGACY__NUTE_GUNRAYS_BOUNTY__FORCE_LOSS;
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, lossActionId)
                && Filters.escortedBy(self, Filters.alien).accepts(game, character)) {
            final String opponent = game.getOpponent(playerId);
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, lossActionId);
            action.setText("Make opponent lose 1 Force");
            action.setActionMsg("Make opponent lose 1 Force, since " + GameUtils.getCardLink(character) + " is escorted by an alien");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new LoseForceEffect(action, opponent, 1));
            actions.add(action);
        }
        return actions;
    }
}
