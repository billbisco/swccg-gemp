package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CaptureCharacterOnTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToFireWeaponModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.RelocatedBetweenLocationsResult;
import java.util.List;

/** Legacy Block 6.3: Jodo Kast (V). */
public class Card1263_005 extends AbstractAlien {
    public Card1263_005() {
        super(Side.DARK, 2, 4, 3, 3, 5, Title.Jodo, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setVirtualSuffix(true);
        setArmor(5);
        setLore("Bounty Hunter and scout. Perfectionist. Equipped with mandalorian armor and a jet pack. He doesn't mind being mistaken for Boba Fett. Occasionally works for Black Sun.");
        setGameText("Once per game, if a character was just 'transported' here by Nabrun Leids, may capture that character. At same site, opponent must first use 1 Force to fire a weapon. May be targeted by Hidden Weapons. May 'fly' (landspeed = 3).");
        addIcons(Icon.PREMIUM, Icon.WARRIOR, Icon.LEGACY_BLOCK_6);
        addKeywords(Keyword.BOUNTY_HUNTER, Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        // The two unchanged printed abilities use the original Jodo implementation's modifiers.
        return List.of(new MayBeTargetedByModifier(self, Title.Hidden_Weapons),
                new DefinedByGameTextLandspeedModifier(self, 3),
                new ExtraForceCostToFireWeaponModifier(self, Filters.and(Filters.opponents(self),
                        Filters.atSameSite(self)), 1));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        GameTextActionId id = GameTextActionId.LEGACY__JODO_KAST_V__CAPTURE;
        if (result instanceof RelocatedBetweenLocationsResult moved
                && moved.getActionSource() != null
                && Filters.Nabrun_Leids.accepts(game, moved.getActionSource())
                && GameConditions.isOncePerGame(game, self, id)) {
            Filter target = Filters.and(Filters.in(moved.getMovedCards()), Filters.character, Filters.here(self));
            if (GameConditions.canTarget(game, self, TargetingReason.TO_BE_CAPTURED, target)) {
                var action = new OptionalGameTextTriggerAction(self, sourceId, id);
                action.setText("Capture transported character");
                action.appendUsage(new OncePerGameEffect(action));
                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose transported character", TargetingReason.TO_BE_CAPTURED, target) {
                    @Override
                    protected void cardTargeted(int targetGroupId, PhysicalCard targetedCard) {
                        action.allowResponses(new RespondableEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                action.appendEffect(new CaptureCharacterOnTableEffect(action, targetingAction.getPrimaryTargetCard(targetGroupId)));
                            }
                        });
                    }
                });
                return List.of(action);
            }
        }
        return null;
    }
}
