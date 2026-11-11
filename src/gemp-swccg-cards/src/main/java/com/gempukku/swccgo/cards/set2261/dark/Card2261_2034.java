package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.common.LegacyVersion;
import java.time.LocalDate;
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
import com.gempukku.swccgo.logic.modifiers.SiteAndCardsThereToUsedPileWhenLeavesTableModifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerDuringBattleModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Legacy Block 6.1 Fleet Security Protocols.
 * PC-E2 version of 1261_34: the PC website card list rewrite (by 2012-07-21): during battle where you have a starship or vehicle, characters at related starship (or vehicle) sites cumulatively add 2 to your total power; any starship (or vehicle) site that leaves table goes to Used Pile with all cards there. The Virtual Cards Master List (2013-04-27, PC-E4) repeats the PC-E1 words (2261_1034) and is the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_2034 extends AbstractNormalEffect {
    private static final Filter DOWNLOADABLE_SITE = Filters.and(Filters.non_unique, Filters.site,
            Filters.or(Filters.starship_site, Filters.vehicle_site));

    public Card2261_2034() {
        super(Side.DARK, 3f, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Fleet Security Protocols",
                Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("Since the debacle at Yavin, the Emperor places a premium on the security of his costly war machine.");
        setGameText("Deploy on table. Once per turn, may deploy a non-unique starship (or vehicle) site from Reserve Deck; reshuffle. During battle where you have a starship or vehicle, characters at related starship (or vehicle) sites cumulatively add 2 to your total power and may be forfeited to reduce battle damage or attrition. When a starship (or vehicle) site leaves table, place it and all cards there in owner's Used Pile. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1261_34", "PC-E2",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
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

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(
                new TotalPowerDuringBattleModifier(self, new DuringBattleWithParticipantCondition(yourVesselsInBattle(self)),
                        new BaseEvaluator() {
                            @Override
                            public float evaluateExpression(GameState state, ModifiersQuerying modifiers, PhysicalCard location) {
                                return 2 * charactersAtRelatedSites(state.getGame(), self).size();
                            }
                        }, self.getOwner()),
                new SiteAndCardsThereToUsedPileWhenLeavesTableModifier(self,
                        Filters.and(Filters.site, Filters.or(Filters.starship_site, Filters.vehicle_site))));
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
