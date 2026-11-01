package com.gempukku.swccgo.cards.set1114.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.CancelCardBeingPlayedEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 14: Armed & Operational
 * Type: Character
 * Subtype: Jedi Master
 * Title: Yoda, Master Of The Force
 */
public class Card1114_015 extends AbstractJediMaster {
    public Card1114_015() {
        super(Side.LIGHT, 4, 5, 3, 7, 7, "Yoda, Master Of The Force", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_14, Rarity.V);
        setVirtualSuffix(true);
        setLore("Jedi Council member.");
        setGameText("Deploys only to battlegrounds. Power +3 in defensive battles. You may not deploy [Episode I] Jedi. While with a Wookiee, adds one battle destiny. Once per game, may lose 2 Force from Force Pile to cancel a non-[Immune to Sense] Interrupt. Immune to attrition.");
        addPersona(Persona.YODA);
        addIcons(Icon.REFLECTIONS_III, Icon.WARRIOR);
        addKeywords(Keyword.JEDI_COUNCIL_MEMBER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.battleground;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition defensive = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return GameConditions.isDuringBattleInitiatedBy(gameState.getGame(), gameState.getGame().getOpponent(playerId));
            }
        };
        Condition withWookiee = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return Filters.canSpot(gameState.getGame(), self, Filters.and(Filters.Wookiee, Filters.presentWith(self)));
            }
        };
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, defensive, 3));
        modifiers.add(new MayNotDeployModifier(self, Filters.and(Icon.EPISODE_I, Filters.Jedi), playerId));
        modifiers.add(new AddsBattleDestinyModifier(self, withWookiee, 1, playerId));
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        if (!(effect instanceof RespondablePlayingCardEffect)) {
            return null;
        }
        PhysicalCard played = ((RespondablePlayingCardEffect) effect).getCard();
        if (!Filters.and(Filters.Interrupt, Filters.not(Filters.immune_to_Sense)).accepts(game.getGameState(), game.getModifiersQuerying(), played)
                || !GameConditions.isOncePerGame(game, self, GameTextActionId.OTHER_CARD_ACTION_1)) {
            return null;
        }
        final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_1);
        action.setText("Cancel " + played.getTitle());
        action.appendUsage(new OncePerGameEffect(action));
        action.appendCost(new LoseForceEffect(action, playerId, 2));
        action.appendEffect(new CancelCardBeingPlayedEffect(action, (RespondablePlayingCardEffect) effect));
        return Collections.singletonList(action);
    }
}
