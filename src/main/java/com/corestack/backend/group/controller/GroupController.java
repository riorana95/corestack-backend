package com.corestack.backend.group.controller;

import com.corestack.backend.group.dto.*;
import com.corestack.backend.group.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public List<GroupSummaryResponse> listMyGroups() {
        return groupService.listMyGroups();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupDetailResponse createGroup(@Valid @RequestBody CreateGroupRequest request) {
        return groupService.createGroup(request);
    }

    @GetMapping("/{groupId}")
    public GroupDetailResponse getGroup(@PathVariable UUID groupId) {
        return groupService.getGroup(groupId);
    }

    @PutMapping("/{groupId}")
    public GroupDetailResponse updateGroup(
            @PathVariable UUID groupId,
            @Valid @RequestBody UpdateGroupRequest request) {
        return groupService.updateGroup(groupId, request);
    }

    @PostMapping("/{groupId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public GroupMemberResponse addMember(
            @PathVariable UUID groupId,
            @Valid @RequestBody AddMemberRequest request) {
        return groupService.addMember(groupId, request);
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(@PathVariable UUID groupId, @PathVariable UUID userId) {
        groupService.removeMember(groupId, userId);
    }

    @GetMapping("/{groupId}/users/search")
    public List<UserSearchResponse> searchUsers(
            @PathVariable UUID groupId,
            @RequestParam String q) {
        return groupService.searchUsersForInvite(groupId, q);
    }
}
