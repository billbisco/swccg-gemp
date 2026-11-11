package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleWithParticipantCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.SiteAndCardsThereToUsedPileWhenLeavesTableModifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerDuringBattleModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Legacy Block 6.1 Fleet Security Protocols.
 * Text: PC-E4, the Virtual Cards Master List (2013-04-27), which repeats the revised 2010 sheet words (PC-E1, 2261_1034).
 * Older texts: original 2261_34 (2009 sheet), PC-E1 2261_1034 (2010 sheet), PC-E2 2261_2034 (PC website card list, 2012), PC-E3 2261_3034 (PC card image, by 2013-02-25)
 * (Bill 2026-11-11: every publication is a version).
 */
public class Card1261_034 extends AbstractNormalEffect {
    private static final Filter DOWNLOADABLE_SITE = Filters.and(Filters.non_unique, Filters.site,
            Filters.or(Filters.starship_site, Filters.vehicle_site));

    public Card1261_034() {
        super(Side.DARK, 3f, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Fleet Security Protocols",
                Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("Since the debacle at Yavin, the Emperor places a premium on the security of his costly war machine.");
        setGameText("Deploy on table. During battle involving your starship (or vehicle), it is power +2 for each of your characters at its related sites (they may be forfeited to reduce battle damage and attrition). Once per turn, may ▼ a non-unique starship (or vehicle) site (if such sites leave table, place them and all your cards there in Used Pile). (Immune to Alter.)");
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Alter);
    }

    private static Filter yourVesselsInBattle(PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.or(Filters.starship, Filters.vehicle), Filters.participatingInBattle);
    }

    /** Your characters at sites related to your starships and vehicles participating in the battle. */
    private static Collection<PhysicalCard> charactersAtRelatedSites(SwccgGame game, PhysicalCard self) {
        Set<PhysicalCard> characters = new LinkedHashSet<>();
        if (!game.getGameState().isDuringBattle())
            return characters;
        for (PhysicalCard vessel : Filters.filterActive(game, self, yourVesselsInBattle(self))) {
            characters.addAll(Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.character,
                    Filters.at(Filters.siteOfStarshipOrVehicle(vessel)))));
        }
        return characters;
    }

    /** Master List (PC-E4) = the revised 2010 sheet (PC-E1): your starship (or vehicle) in battle is power +2 for each of your characters at its related sites; only non-unique starship (or vehicle) sites leaving table take your cards there to Used Pile. */
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(
                new PowerModifier(self, yourVesselsInBattle(self),
                        new BaseEvaluator() {
                            @Override
                            public float evaluateExpression(GameState state, ModifiersQuerying modifiers, PhysicalCard vessel) {
                                if (!state.isDuringBattle())
                                    return 0;
                                return 2 * Filters.countActive(state.getGame(), self, Filters.and(Filters.your(self),
                                        Filters.character, Filters.at(Filters.siteOfStarshipOrVehicle(vessel))));
                            }
                        }),
                new SiteAndCardsThereToUsedPileWhenLeavesTableModifier(self, DOWNLOADABLE_SITE, null, self.getOwner()));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (!TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                || !(GameConditions.isBattleDamageRemaining(game, playerId) || GameConditions.isAttritionRemaining(game, playerId))
                || !GameConditions.isDuringBattleWithParticipant(game, yourVesselsInBattle(self)))
            return null;
        Collection<PhysicalCard> forfeitable = Filters.filter(charactersAtRelatedSites(game, self), game,
                Filters.not(Filters.participatingInBattle));
        if (forfeitable.isEmpty())
            return null;
        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
        action.setText("Forfeit character at related site");
        action.appendTargeting(new ChooseCardOnTableEffect(action, playerId, "Choose character to forfeit", forfeitable) {
            @Override
            protected void cardSelected(PhysicalCard selectedCard) {
                action.setActionMsg(null);
                action.appendEffect(new ForfeitCardFromTableEffect(action, selectedCard));
            }
        });
        return List.of(action);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
            PhysicalCard self, int sourceId) {
        var id = GameTextActionId.LEGACY__FLEET_SECURITY_PROTOCOLS__DEPLOY_VESSEL_SITE;
        if (!GameConditions.isOncePerTurn(game, self, playerId, sourceId, id)
                || !GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id))
            return null;
        var action = new TopLevelGameTextAction(self, playerId, sourceId, id);
        action.setText("Deploy non-unique starship or vehicle site");
        action.appendUsage(new OncePerTurnEffect(action));
        action.appendEffect(new DeployCardFromReserveDeckEffect(action, DOWNLOADABLE_SITE, true));
        return List.of(action);
    }
}
