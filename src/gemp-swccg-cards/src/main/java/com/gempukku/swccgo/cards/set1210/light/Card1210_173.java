package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfPlayersNextTurnActionProxyEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.modifiers.IgnoresLocationDeploymentRestrictionsInGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.EndOfTurnResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Location
 * Subtype: System
 * Title: Alderaan (Blown Away)
 */
public class Card1210_173 extends AbstractSystem {
    public Card1210_173() {
        super(Side.LIGHT, Title.Alderaan_Blown_Away, 2, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setLocationLightSideGameText("May not be deployed if an Alderaan site (or sector) on table. Star Cruisers ignore location deployment restrictions in their game text.");
        setLocationDarkSideGameText("When deployed, you choose: flip Set Your Course For Alderaan immediately or at the end of your next turn.");
        setGameText("Light: May not be deployed if an Alderaan site (or sector) on table. Star Cruisers ignore location deployment restrictions in their game text. Dark: When deployed, you choose: flip Set Your Course For Alderaan immediately or at the end of your next turn.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 0);
        addIcons(Icon.SPACE, Icon.SPECIAL_EDITION, Icon.DAGOBAH);
    }

    @Override
    public List<Modifier> getAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = super.getAlwaysOnModifiers(game, self);
        modifiers.add(new MayNotDeployModifier(self, new OnTableCondition(self, Filters.or(
                Filters.Alderaan_site,
                Filters.and(Filters.sector, Filters.partOfSystem(Title.Alderaan))))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IgnoresLocationDeploymentRestrictionsInGameTextModifier(self, Filters.Star_Cruiser));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredAfterTriggers(final String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, final PhysicalCard self, final int gameTextSourceCardId) {
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canSpot(game, self, Filters.Set_Your_Course_For_Alderaan)) {

            final PhysicalCard sycfa = Filters.findFirstActive(game, self, Filters.Set_Your_Course_For_Alderaan);
            final int permCardId = self.getPermanentCardId();
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.LEGACY__ALDERAAN_BLOWN_AWAY__FLIP_SET_YOUR_COURSE);
            action.setText("Choose when to flip Set Your Course For Alderaan");
            action.appendEffect(
                    new PlayoutDecisionEffect(action, playerOnDarkSideOfLocation,
                            new MultipleChoiceAwaitingDecision("Flip Set Your Course For Alderaan", new String[]{"Immediately", "At the end of your next turn"}) {
                                @Override
                                protected void validDecisionMade(int index, String result) {
                                    if (index == 0) {
                                        action.appendEffect(
                                                new FlipCardEffect(action, sycfa));
                                    }
                                    else {
                                        action.appendEffect(
                                                new AddUntilEndOfPlayersNextTurnActionProxyEffect(action,
                                                        new AbstractActionProxy() {
                                                            @Override
                                                            public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                                                PhysicalCard location = game.findCardByPermanentId(permCardId);
                                                                if (effectResult instanceof EndOfTurnResult
                                                                        && playerOnDarkSideOfLocation.equals(game.getGameState().getCurrentPlayerId())
                                                                        && GameConditions.canSpot(game, location, Filters.Set_Your_Course_For_Alderaan)) {
                                                                    PhysicalCard objective = Filters.findFirstActive(game, location, Filters.Set_Your_Course_For_Alderaan);
                                                                    RequiredGameTextTriggerAction flipAction = new RequiredGameTextTriggerAction(location, gameTextSourceCardId);
                                                                    flipAction.setText("Flip Set Your Course For Alderaan");
                                                                    flipAction.appendEffect(
                                                                            new FlipCardEffect(flipAction, objective));
                                                                    return Collections.singletonList(flipAction);
                                                                }
                                                                return null;
                                                            }
                                                        }, playerOnDarkSideOfLocation));
                                    }
                                }
                            }));
            return Collections.singletonList(action);
        }
        return null;
    }
}
