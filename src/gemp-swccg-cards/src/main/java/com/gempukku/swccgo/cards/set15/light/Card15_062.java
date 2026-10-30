package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.set5.light.Card5_031;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromHandEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.List;
import java.util.ArrayList;

public class Card15_062 extends Card5_031 {
    public Card15_062() {
        setLore(""); addIcon(Icon.CLOUD_CITY,0); addIcons(Icon.REFLECTIONS_GOLD);
        setTitleComponents(new TitleComponent("Armed And Dangerous",Uniqueness.UNRESTRICTED),new TitleComponent(Title.Ambush,Uniqueness.UNIQUE));
        setGameText("During your turn, target a site where your total power is more than double opponent's total power. Unless opponent has a Dark Jedi or character weapon there, place each opponent character, vehicle, and starship there (and cards on them) in owner's Used Pile. OR If a battle or duel was just initiated at a site, search your hand or Reserve Deck for one unique weapon matching one of your participating characters. Immediately deploy that weapon (for free) on that character and reshuffle (if from Reserve Deck).");
    }
    @Override public String getTitle() { return "Armed And Dangerous & Ambush"; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId,SwccgGame game,EffectResult result,PhysicalCard self) {
        boolean duel=TriggerConditions.duelInitiatedAt(game,result,Filters.site);
        if (!duel && !TriggerConditions.battleInitiatedAt(game,result,Filters.site)) return null;
        Filter targets=Filters.and(Filters.your(self),Filters.character,duel?Filters.participatingInDuel:Filters.participatingInBattle);
        if (!GameConditions.canSpot(game,self,targets)) return null;
        List<PlayInterruptAction> actions=new ArrayList<>();
        List<PhysicalCard> handTargets=new ArrayList<>();
        for(PhysicalCard character:Filters.filterActive(game,self,targets)) {
            if(GameConditions.hasInHand(game,playerId,Filters.and(Filters.unique,Filters.matchingWeaponForCharacter(character),Filters.deployableToTarget(self,Filters.sameCardId(character),true,0))))
                handTargets.add(character);
        }
        if (!handTargets.isEmpty()) actions.add(weaponAction(playerId,game,self,Filters.in(handTargets),false));
        if (GameConditions.canDeployCardFromReserveDeck(game,playerId,self,GameTextActionId.ARMED_AND_DANGEROUS__DOWNLOAD_WEAPON_FROM_RESERVE_DECK,true,false))
            actions.add(weaponAction(playerId,game,self,targets,true));
        return actions.isEmpty()?null:actions;
    }
    private PlayInterruptAction weaponAction(String playerId,SwccgGame game,PhysicalCard self,Filter targets,boolean reserve) {
        PlayInterruptAction action=new PlayInterruptAction(game,self,GameTextActionId.ARMED_AND_DANGEROUS__DOWNLOAD_WEAPON_FROM_RESERVE_DECK);
        action.setText("Deploy matching character weapon from "+(reserve?"Reserve Deck":"hand"));
        action.appendTargeting(new TargetCardOnTableEffect(action,playerId,"Choose character for matching weapon",targets) {
            @Override protected void cardTargeted(int targetGroup,PhysicalCard character) {
                action.allowResponses(new RespondablePlayCardEffect(action) {
                    @Override protected void performActionResults(Action targetingAction) {
                        PhysicalCard target=action.getPrimaryTargetCard(targetGroup);
                        Filter weapon=Filters.and(Filters.unique,Filters.matchingWeaponForCharacter(target));
                        if(reserve) action.appendEffect(new DeployCardToTargetFromReserveDeckEffect(action,weapon,Filters.sameCardId(target),true,true));
                        else action.appendEffect(new DeployCardToTargetFromHandEffect(action,playerId,weapon,Filters.sameCardId(target),true));
                    }
                });
            }
        });
        return action;
    }
}
