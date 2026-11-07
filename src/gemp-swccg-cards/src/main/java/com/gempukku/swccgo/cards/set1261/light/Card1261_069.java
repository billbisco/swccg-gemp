package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddCardsToMoveUsingLandspeedSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.MovingUsingLandspeedResult;

import java.util.List;

/** Leia, Optimistic Leader, final Light master page 10 and complete official face. */
public class Card1261_069 extends AbstractRebel {
    private static final Filter SPORTING_BLASTER = Filters.title(Title.Leias_Sporting_Blaster);

    public Card1261_069() {
        super(Side.LIGHT, 1, 3, 3, 3, 7, "Leia, Optimistic Leader", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("Spy. After escaping Detention Block AA-23, Rebels found themselves in the trash compactor. Leia pointed out, \"It could be worse.\" It soon was.");
        setGameText("Adds 1 to power of anything she pilots. Dark Jedi deploy are +1 here. May move as a 'react' (one other Rebel present may move with her). While armed with Leia's Sporting Blaster, its weapon destiny draws are +1 and Leia is power +1. Immune to attrition < 3.");
        addPersona(Persona.LEIA);
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.SPY, Keyword.FEMALE);
        setSpecies(Species.ALDERAANIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new AddsPowerToPilotedBySelfModifier(self, 1),
                new DeployCostToLocationModifier(self, Filters.Dark_Jedi, 1, Filters.sameLocation(self)),
                new MayMoveAsReactModifier(self),
                new EachWeaponDestinyForWeaponFiredByModifier(self, 1, SPORTING_BLASTER, Filters.any),
                new PowerModifier(self, new ArmedWithCondition(self, SPORTING_BLASTER), 1),
                new ImmuneToAttritionLessThanModifier(self, 3));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult result, PhysicalCard self, int gameTextSourceCardId) {
        if (!TriggerConditions.movingUsingLandspeed(game, result, self)) {
            return null;
        }
        var movement = (MovingUsingLandspeedResult) result;
        var react = game.getGameState().getMoveAsReactState();
        if (!movement.isReact() || react == null || !react.canContinue() || !self.equals(react.getReactingCard())) {
            return null;
        }
        Filter companion = Filters.and(Filters.your(self), Filters.Rebel, Filters.other(self), Filters.present(self),
                Filters.not(Filters.in(movement.getAllCardsMoving())), Filters.not(Filters.in(react.getCardsParticipatingInReact())),
                Filters.notPreventedFromParticipatingInReact,
                Filters.movableAsRegularMoveUsingLandspeed(playerId, true, movement.isMoveAway(), true, 0, null,
                        Filters.sameCardId(movement.getMovingTo())));
        if (!GameConditions.canTarget(game, self, companion)) {
            return null;
        }

        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
        action.setText("Move one Rebel with Leia");
        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose one other Rebel present to move with Leia", companion) {
            @Override
            protected void cardTargeted(int group, PhysicalCard rebel) {
                action.allowResponses(new UnrespondableEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(new AddCardsToMoveUsingLandspeedSimultaneouslyEffect(action, List.of(rebel), movement));
                    }
                });
            }
        });
        return List.of(action);
    }
}
