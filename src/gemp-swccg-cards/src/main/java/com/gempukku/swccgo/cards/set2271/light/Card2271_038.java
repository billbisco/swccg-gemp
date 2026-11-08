package com.gempukku.swccgo.cards.set2271.light;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceAtLocationFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RestoreCardToNormalEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToCardTitleWithOwnerModifier;
import com.gempukku.swccgo.logic.modifiers.LegacyMayPlayInterruptAsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetForfeitModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.time.LocalDate;
import java.util.List;

/**
 * Field Dressing (Virtual Block 7.1).
 * Pre-errata version of 1271_38 (Virtual Block 7.1 release sheet text, Jun 2010), replaced by the Virtual Cards Master List text (return the character from Lost Pile). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_038 extends AbstractImmediateEffect {
    public Card2271_038() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Field Dressing", Uniqueness.RESTRICTED_2, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setLore("The Alliance lost many fine soldiers at conflicts such as the Battle of Hoth. The Rebel High Command is aware of the cost of freedom from Imperial tyranny.");
        setGameText("If you just forfeited a character of ability < 5 present at a site, deploy on that character (who remains at same site and, if 'hit,' is restored to normal). Character is forfeit = 0 (even if its forfeit may not normally be reduced). May play First Aid to place this card in Used Pile. Immune to your Control.");
        setLegacyVersion(new LegacyVersion("1271_38", "",
                "Virtual Block 7.1 release sheet (2010-06-18)", LocalDate.of(2010, 6, 18), LocalDate.of(2013, 4, 27)));
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new ImmuneToCardTitleWithOwnerModifier(self, Title.Control, self.getOwner()));
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game, EffectResult result,
            PhysicalCard self, int sourceId) {
        // Original text: the character "remains at same site" (it is not 'revived' from Lost Pile, so a
        // may-not-be-revived rule does not stop it). GEMP has already put the forfeited character in Lost Pile at this
        // point, so it is put back at the same site, which is the closest the engine can do.
        if (!playerId.equals(result.getPerformingPlayerId())
                || !TriggerConditions.justForfeitedToLostPileFromLocation(game, result, Filters.and(Filters.your(self),
                    Filters.character, Filters.abilityLessThan(5), Filters.canBeTargetedBy(self)), Filters.site)) return null;

        LostFromTableResult forfeiture = (LostFromTableResult) result;
        if (forfeiture.getFromAttachedTo() != null) return null;
        PhysicalCard patient = forfeiture.getCard();
        PhysicalCard site = forfeiture.getFromLocation();

        PlayCardAction action = getPlayCardAction(playerId, game, self, self, forceMayDeployForFree(), 0, null, null, null,
            null, null, false, 0, Filters.any, null);
        if (action == null) return null;

        // The patient is still in Lost Pile during the Immediate Effect's cancellation window.
        action.setText("Deploy Field Dressing on just-forfeited character");
        action.appendBeforeCost(new SetWhileInPlayDataEffect(action, self, new WhileInPlayData(List.of(patient, site))));
        return List.of(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result,
            PhysicalCard self, int sourceId) {
        if (!TriggerConditions.justDeployed(game, result, self) || self.getWhileInPlayData() == null) return null;

        var saved = self.getWhileInPlayData().getPhysicalCards();
        PhysicalCard patient = saved.get(0);
        PhysicalCard site = saved.get(1);
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Forfeited character remains at same site; attach Field Dressing");
        action.appendEffect(new PlaceAtLocationFromLostPileEffect(action, self.getOwner(), patient, site, false, true));
        action.appendEffect(new PassthruEffect(action) {
            @Override protected void doPlayEffect(SwccgGame game) {
                if (Filters.at(site).accepts(game, patient) && patient.getZone().isInPlay()) {
                    if (patient.isHit()) action.appendEffect(new RestoreCardToNormalEffect(action, patient));
                    action.appendEffect(new AttachCardFromTableEffect(action, self, patient));
                } else {
                    action.appendEffect(new LoseCardFromTableEffect(action, self));
                }
            }
        });
        return List.of(action);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new ResetForfeitModifier(self, Filters.hasAttached(self), null, 0, true),
            new LegacyMayPlayInterruptAsModifier(self, Filters.and(Filters.your(self), Filters.title("First Aid")),
                CardSubtype.USED, new LegacyMayPlayInterruptAsModifier.PlayMode() {
                    @Override public PlayInterruptAction createAction(String playerId, SwccgGame game, PhysicalCard source,
                            PhysicalCard interrupt) {
                        if (source.getAttachedTo() == null || !GameConditions.canUseForceToPlayInterrupt(game, playerId, interrupt, 0)) return null;
                        PlayInterruptAction action = new PlayInterruptAction(game, interrupt);
                        action.setText("Place Field Dressing in Used Pile");
                        action.allowResponses(new RespondablePlayCardEffect(action) {
                            @Override protected void performActionResults(Action targetingAction) {
                                action.appendEffect(new PlaceCardInUsedPileFromTableEffect(action, source));
                            }
                        });
                        return action;
                    }
                }));
    }
}
