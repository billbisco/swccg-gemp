package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.set1112.dark.Card1112_024;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.*;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;
import java.util.ArrayList;
import java.util.List;

/** Legacy Block 5 retains the predecessor's printed stats and attrition immunity. */
public class Card1250_002 extends Card1112_024 {
    public Card1250_002() {
        setGameText("Once during opponent's activate phase, may relocate Aurra to a site where opponent has a Jedi. Once per game, during your move phase, may fire her weapon at a Jedi; hit target is lost. Immune to attrition < 4.");
        addIcon(Icon.LEGACY_BLOCK_5);
        setAsLegacy(true);
    }

    @Override
    public ExpansionSet getExpansionSet() { return ExpansionSet.VIRTUAL_BLOCK_5; }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        List<TopLevelGameTextAction> actions = new ArrayList<>();
        Filter destination = new Filter() {
            @Override
            public boolean accepts(GameState state, ModifiersQuerying querying, PhysicalCard site) {
                return Filters.site.accepts(state, querying, site)
                        && GameConditions.canSpot(game, self, Filters.and(Filters.opponents(self), Filters.Jedi, Filters.at(site)))
                        && Filters.canBeRelocatedToLocation(site, false, 0).accepts(state, querying, self);
            }
        };
        if (GameConditions.isOnceDuringOpponentsPhase(game, self, playerId, sourceId, Phase.ACTIVATE)
                && GameConditions.canSpot(game, self, destination)) {
            var action = new TopLevelGameTextAction(self, playerId, sourceId);
            action.setText("Relocate to opponent's Jedi site");
            action.appendUsage(new OncePerPhaseEffect(action));
            action.appendEffect(new ChooseCardOnTableEffect(action, playerId, "Choose site with opponent's Jedi", destination) {
                @Override
                protected void cardSelected(PhysicalCard site) {
                    action.appendEffect(new RelocateBetweenLocationsEffect(action, self, site));
                }
            });
            actions.add(action);
        }

        GameTextActionId id = GameTextActionId.LEGACY__AURRA_SING_V__FIRE_WEAPON;
        Filter weapon = Filters.and(Filters.weapon, Filters.attachedTo(self),
                Filters.canBeFiredAt(self, Filters.and(Filters.Jedi, Filters.canBeTargetedBy(self)), 0));
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.MOVE)
                && GameConditions.isOncePerGame(game, self, id)
                && GameConditions.canSpot(game, self, weapon)) {
            var action = new TopLevelGameTextAction(self, playerId, sourceId, id);
            action.setText("Fire weapon at a Jedi");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new ChooseCardOnTableEffect(action, playerId, "Choose Aurra's weapon", weapon) {
                @Override
                protected void cardSelected(PhysicalCard selectedWeapon) {
                    action.appendEffect(new FireWeaponEffect(action, selectedWeapon, false, Filters.Jedi) {
                        @Override
                        protected List<ActionProxy> getWeaponFiringActionProxies(String firingPlayer, SwccgGame game, PhysicalCard firedWeapon) {
                            return List.of(new AbstractActionProxy() {
                                @Override
                                public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult result) {
                                    if (TriggerConditions.justHitBy(game, result, Filters.Jedi, firedWeapon, self)) {
                                        var lose = new RequiredGameTextTriggerAction(self, sourceId);
                                        lose.setText("Lose hit Jedi");
                                        lose.appendEffect(new LoseCardFromTableEffect(lose, ((HitResult)result).getCardHit()));
                                        return List.of(lose);
                                    }
                                    return null;
                                }
                            });
                        }
                    });
                }
            });
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        // The printed loss belongs only to the once-game move-phase shot, not ordinary battle fire.
        return null;
    }
}
