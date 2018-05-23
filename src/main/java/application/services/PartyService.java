package application.services;


import application.models.User;
import application.session.Party;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;


@Service
public class PartyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PartyService.class);

    private final ConcurrentHashMap<Long, Party> parties = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Long> userIdToPartyId = new ConcurrentHashMap<>();


    public void createPartyWithLeader(@NotNull User user) {
        LOGGER.info("createParty: leader.id=" + user.getId() + ", .login=" + user.getLogin());
        LOGGER.info("           : before operations: parties.size=" + parties.size());
        LOGGER.info("                              : userIdToPartyId.size=" + userIdToPartyId.size());

        parties.put(user.getId(), new Party(user));
        userIdToPartyId.put(user.getId(), user.getId());

        LOGGER.info("           : after operations: parties.size=" + parties.size());
        LOGGER.info("                             : userIdToPartyId.size=" + userIdToPartyId.size());
        LOGGER.info("createParty: done");
    }

    public Long getPartyLeaderId(@NotNull User user) {
        LOGGER.info("getPartyLeaderId: user.id=" + user.getId() + ", .login=" + user.getLogin());

        final Long partyLeaderId = userIdToPartyId.getOrDefault(user.getId(), null);
        LOGGER.info("                : partyLeaderId=" + ((partyLeaderId == null) ? "nil" : partyLeaderId));

        return partyLeaderId;
    }

    @Nullable
    public Party getParty(@NotNull Long leaderId) {
        LOGGER.info("getParty: leaderId=" + leaderId);

        final Party party = parties.getOrDefault(leaderId, null);
        LOGGER.info("        : party is " + ((party == null) ? "null" : "not null"));
        if (party != null) {
            LOGGER.info("        : party.leader.id=" + party.getLeader().getId());
        }

        return party;
    }
}
