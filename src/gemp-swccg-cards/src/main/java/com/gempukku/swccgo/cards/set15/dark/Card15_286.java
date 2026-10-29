package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.set5.dark.Card5_138;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import java.util.LinkedList;
import java.util.List;

public class Card15_286 extends Card5_138 {
    public Card15_286() {
        setCardSubtype(CardSubtype.USED_OR_LOST);
        setAlternateDestiny(3);
        setLore("");
        setGameText("USED: One of your protocol Droids 'insults' one character present, canceling that character's game text for remainder of turn. OR During your battle phase, initiate a battle where both players have droids (but no presence or spies). Both sides add one battle destiny. LOST: Use 3 Force to relocate any Effect (except those immune to Alter) deployed on a location to another appropriate location.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD);
        addComboCardTitles("Surprise", "E Chu Ta");
    }
    @Override public String getTitle() { return "Surprise & E Chu Ta"; }
    @Override public Float getDestiny() { return 3f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNRESTRICTED; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        List<PlayInterruptAction> usedActions = super.getGameTextTopLevelActions(playerId, game, self);
        if (usedActions != null) {
            usedActions.forEach(action -> action.setPlayedAsSubtype(CardSubtype.USED));
            actions.addAll(usedActions);
        }

        // Check condition(s)
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3)) {

            Filter effectFilter = Filters.and(Filters.Effect, Filters.except(Filters.immune_to_Alter), Filters.attachedTo(Filters.location),
                    Filters.effectCanBeRelocatedTo(playerId, Filters.and(Filters.location, Filters.canBeTargetedBy(self))));

            if (GameConditions.canTarget(game, self, effectFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Relocate Effect");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Effect to relocate", effectFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard targetedEffect) {
                                action.addAnimationGroup(targetedEffect);
                                Filter locationFilter = Filters.and(Filters.location, Filters.not(Filters.hasAttached(targetedEffect)), Filters.canRelocateEffectTo(playerId, targetedEffect));
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose location to relocated Effect to", locationFilter) {
                                            @Override
                                            protected void cardTargeted(final int targetGroupId2, PhysicalCard targetedLocation) {
                                                action.addAnimationGroup(targetedLocation);
                                                // Set target filters for re-targeting
                                                action.updatePrimaryTargetFilter(targetGroupId1, Filters.and(Filters.Effect, Filters.except(Filters.immune_to_Alter), Filters.attachedTo(Filters.location),
                                                        Filters.effectCanBeRelocatedTo(playerId, Filters.and(Filters.inActionTargetGroup(action, targetGroupId2), Filters.canBeTargetedBy(self)))));
                                                action.updatePrimaryTargetFilter(targetGroupId2, Filters.and(Filters.location, Filters.canRelocateEffectTo(playerId, Filters.inActionTargetGroup(action, targetGroupId1))));
                                                // Pay cost(s)
                                                action.appendCost(
                                                        new UseForceEffect(action, playerId, 3));
                                                // Allow response(s)
                                                action.allowResponses("Relocate " + GameUtils.getCardLink(targetedEffect) + " to " + GameUtils.getCardLink(targetedLocation),
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                                PhysicalCard finalEffect = action.getPrimaryTargetCard(targetGroupId1);
                                                                PhysicalCard finalLocation = action.getPrimaryTargetCard(targetGroupId2);

                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new AttachCardFromTableEffect(action, finalEffect, finalLocation));
                                                            }
                                                        }
                                                );
                                            }
                                        });
                            }
                        });
                actions.add(action);
            }
        }

        return actions;
    }
}
