package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToAttritionEffect;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.SubstituteDestinyEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Han Solo, Courageous Smuggler, final Legacy master page 8. */
public class Card1261_066 extends AbstractRebel {
    private static final GameTextActionId DEPLOY_CHEWIE = GameTextActionId.LEGACY__HAN_SOLO_COURAGEOUS_SMUGGLER__DEPLOY_CHEWIE;

    public Card1261_066() {
        super(Side.LIGHT, 1, 3, 4, 3, 6, "Han Solo, Courageous Smuggler", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setArmor(5);
        setLore("Han's smuggling adventures in Corporate Sector and Hutt Space put him in many tight scrapes. He's about to be in another.");
        setGameText("Adds 3 to power of anything he pilots. While piloting Falcon, may add one destiny to total power or attrition. If Han is about to draw a card for weapon destiny, may instead use Han's ability number. Once per game, may deploy non-[Maintenance] Chewie to same site from Reserve Deck; reshuffle. Immune to attrition < 3.");
        addPersona(Persona.HAN);
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.SMUGGLER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new AddsPowerToPilotedBySelfModifier(self, 3), new ImmuneToAttritionLessThanModifier(self, 3));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new ArrayList<>();
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(self, Filters.piloting(Filters.Falcon)))
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_1)) {
            if (GameConditions.canAddDestinyDrawsToPower(game, playerId)) {
                TopLevelGameTextAction action = destinyAction(self, playerId, gameTextSourceCardId, "Add one destiny to total power");
                action.appendEffect(new AddDestinyToTotalPowerEffect(action, 1));
                actions.add(action);
            }
            if (GameConditions.canAddDestinyDrawsToAttrition(game, playerId)) {
                TopLevelGameTextAction action = destinyAction(self, playerId, gameTextSourceCardId, "Add one destiny to attrition");
                action.appendEffect(new AddDestinyToAttritionEffect(action, 1));
                actions.add(action);
            }
        }
        if (Filters.at(Filters.site).accepts(game, self)
                && GameConditions.isOncePerGame(game, self, DEPLOY_CHEWIE)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, DEPLOY_CHEWIE, Persona.CHEWIE)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, DEPLOY_CHEWIE);
            action.setText("Deploy non-Maintenance Chewie to this site");
            action.appendUsage(new OncePerGameEffect(action));
            Filter target = Filters.and(Filters.character, Filters.Chewie, Filters.not(Icon.MAINTENANCE));
            action.appendEffect(new DeployCardToTargetFromReserveDeckEffect(action, target, Filters.and(Filters.site, Filters.sameSite(self)), true));
            actions.add(action);
        }
        return actions;
    }

    private TopLevelGameTextAction destinyAction(PhysicalCard self, String playerId, int sourceId, String text) {
        TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, sourceId, GameTextActionId.OTHER_CARD_ACTION_1);
        action.setText(text);
        action.appendUsage(new OncePerBattleEffect(action));
        return action;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult result, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isAboutToDrawWeaponDestiny(game, result, playerId, Filters.weaponBeingFiredBy(self))
                && GameConditions.canSubstituteDestiny(game)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Use Han's ability for weapon destiny instead");
            action.appendEffect(new SubstituteDestinyEffect(action, game.getModifiersQuerying().getAbility(game.getGameState(), self)));
            return Collections.singletonList(action);
        }
        return null;
    }
}
