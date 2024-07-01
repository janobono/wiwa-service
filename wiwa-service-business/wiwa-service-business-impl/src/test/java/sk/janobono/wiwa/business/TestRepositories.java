package sk.janobono.wiwa.business;

import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.dal.domain.*;
import sk.janobono.wiwa.dal.model.*;
import sk.janobono.wiwa.dal.repository.*;
import sk.janobono.wiwa.model.Authority;
import sk.janobono.wiwa.model.OrderPackageType;
import sk.janobono.wiwa.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class TestRepositories {

    final Map<String, ApplicationImageDo> applicationImages = new HashMap<>();

    final Map<String, String> applicationProperties = new HashMap<>();

    final AtomicLong authorityId = new AtomicLong(1L);
    final List<AuthorityDo> authorities = new LinkedList<>();
    final Map<Long, List<Long>> userAuthorities = new HashMap<>();

    final Map<Long, List<Long>> boardCodeListItems = new HashMap<>();

    final AtomicLong codeListId = new AtomicLong(1L);
    final List<CodeListDo> codeLists = new LinkedList<>();

    final AtomicLong codeListItemId = new AtomicLong(1L);
    private final List<CodeListItemDo> codeListItems = new LinkedList<>();

    final Map<Long, BoardImageDo> boardImages = new HashMap<>();

    final AtomicLong boardId = new AtomicLong(1L);
    final List<BoardDo> boards = new LinkedList<>();

    final Map<Long, List<Long>> edgeCodeListItems = new HashMap<>();

    final Map<Long, EdgeImageDo> edgeImages = new HashMap<>();

    final AtomicLong edgeId = new AtomicLong(1L);
    final List<EdgeDo> edges = new LinkedList<>();

    final AtomicLong orderCommentId = new AtomicLong(1L);
    final List<OrderCommentDo> orderComments = new LinkedList<>();

    final AtomicLong orderItemId = new AtomicLong(1L);
    final Map<Long, List<OrderItemDo>> orderItems = new HashMap<>();

    final List<OrderItemSummaryDo> orderItemSummaries = new LinkedList<>();

    final List<OrderMaterialDo> orderMaterials = new LinkedList<>();

    final Map<Long, Long> orderNumbers = new HashMap<>();

    final AtomicLong orderId = new AtomicLong(1L);
    final List<OrderDo> orders = new LinkedList<>();

    final AtomicLong orderStatusId = new AtomicLong(1L);
    final Map<Long, List<OrderStatusDo>> orderStatuses = new HashMap<>();

    final AtomicLong userId = new AtomicLong(1L);
    final List<UserDo> users = new LinkedList<>();

    public void mock(final ApplicationImageRepository applicationImageRepository) {
        Mockito.doAnswer(answer -> {
            final String id = answer.getArgument(0);
            applicationImages.remove(id);
            return null;
        }).when(applicationImageRepository).deleteById(Mockito.anyString());

        Mockito.when(applicationImageRepository.findAll(Mockito.any(Pageable.class)))
                .thenAnswer(answer -> {
                    final Pageable pageable = answer.getArgument(0);
                    return new PageImpl<>(applicationImages.values().stream()
                            .map(item -> new ApplicationImageInfoDo(item.getFileName(), item.getFileType(), item.getThumbnail()))
                            .toList(), pageable, applicationImages.size());
                });

        Mockito.when(applicationImageRepository.findById(Mockito.anyString()))
                .thenAnswer(answer -> {
                    final String id = answer.getArgument(0);
                    return Optional.ofNullable(applicationImages.get(id));
                });

        Mockito.when(applicationImageRepository.save(Mockito.any(ApplicationImageDo.class)))
                .thenAnswer(answer -> {
                    final ApplicationImageDo newItem = answer.getArgument(0);
                    applicationImages.put(newItem.getFileName(), newItem);
                    return newItem;
                });
    }

    public void mock(final ApplicationPropertyRepository applicationPropertyRepository) {
        Mockito.doAnswer(answer -> {
            final String key = answer.getArgument(0);
            applicationProperties.remove(key);
            return null;
        }).when(applicationPropertyRepository).deleteByKey(Mockito.anyString());

        Mockito.when(applicationPropertyRepository.findByKey(Mockito.anyString())).thenAnswer(answer -> {
            final String key = answer.getArgument(0);
            if (applicationProperties.containsKey(key)) {
                return Optional.ofNullable(
                        ApplicationPropertyDo.builder()
                                .key(key)
                                .value(applicationProperties.get(key))
                                .build());
            }
            return Optional.empty();
        });

        Mockito.when(applicationPropertyRepository.save(Mockito.any(ApplicationPropertyDo.class))).thenAnswer(answer -> {
            final ApplicationPropertyDo applicationProperty = answer.getArgument(0);
            applicationProperties.put(applicationProperty.getKey(), applicationProperty.getValue());
            return applicationProperty;
        });
    }

    public void mock(final AuthorityRepository authorityRepository) {
        Mockito.when(authorityRepository.count()).thenReturn(authorities.size());

        Mockito.when(authorityRepository.findAll()).thenReturn(authorities);

        Mockito.when(authorityRepository.findByUserId(Mockito.anyLong()))
                .thenAnswer(answer -> {
                    final Long id = answer.getArgument(0);
                    final List<Long> authorityIds = userAuthorities.getOrDefault(id, Collections.emptyList());
                    return authorityIds.stream()
                            .flatMap(authorityId -> authorities.stream().filter(item -> item.getId().equals(authorityId)))
                            .toList();
                });

        Mockito.when(authorityRepository.save(Mockito.any(AuthorityDo.class)))
                .thenAnswer(answer -> {
                    final AuthorityDo newItem = answer.getArgument(0);
                    final Optional<AuthorityDo> savedItem = authorities.stream()
                            .filter(item -> item.getAuthority().equals(newItem.getAuthority()))
                            .findFirst();
                    if (savedItem.isPresent()) {
                        return savedItem.get();
                    }
                    newItem.setId(authorityId.getAndIncrement());
                    authorities.add(newItem);
                    return newItem;
                });

        Mockito.doAnswer(answer -> {
            final Long id = answer.getArgument(0);
            final List<Authority> userAuthoritiesList = answer.getArgument(1);
            userAuthorities.put(id, userAuthoritiesList.stream()
                    .map(a -> authorities.stream().filter(item -> item.getAuthority().equals(a)).findFirst())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(AuthorityDo::getId)
                    .toList()
            );
            return null;
        }).when(authorityRepository).saveUserAuthorities(Mockito.anyLong(), Mockito.any());
    }

    public void mock(final BoardCodeListItemRepository boardCodeListItemRepository) {
        Mockito.when(boardCodeListItemRepository.findByBoardId(Mockito.anyLong()))
                .thenAnswer(answer -> {
                    final Long id = answer.getArgument(0);
                    final List<Long> codeListItemIds = boardCodeListItems.getOrDefault(id, Collections.emptyList());
                    return codeListItemIds.stream()
                            .flatMap(codeListItemId -> codeListItems.stream().filter(item -> item.getId().equals(codeListItemId)))
                            .toList();
                });

        Mockito.doAnswer(answer -> {
            final Long id = answer.getArgument(0);
            final List<Long> codeListItemIds = answer.getArgument(1);
            boardCodeListItems.put(id, codeListItemIds);
            return null;
        }).when(boardCodeListItemRepository).saveAll(Mockito.anyLong(), Mockito.any());
    }

    public void mock(final BoardImageRepository boardImageRepository) {
        Mockito.doAnswer(answer -> {
            final Long id = answer.getArgument(0);
            boardImages.remove(id);
            return null;
        }).when(boardImageRepository).deleteByBoardId(Mockito.anyLong());

        Mockito.when(boardImageRepository.findByBoardId(Mockito.anyLong()))
                .thenAnswer(answer -> {
                    final Long id = answer.getArgument(0);
                    return Optional.ofNullable(boardImages.get(id));
                });

        Mockito.when(boardImageRepository.save(Mockito.any(BoardImageDo.class)))
                .thenAnswer(answer -> {
                    final BoardImageDo newItem = answer.getArgument(0);
                    boardImages.put(newItem.getBoardId(), newItem);
                    return newItem;
                });
    }

    public void mock(final BoardRepository boardRepository) {
        Mockito.when(boardRepository.countByCode(Mockito.anyString())).thenAnswer(answer -> {
            final String code = answer.getArgument(0);
            return boards.stream().filter(item -> item.getCode().equals(code)).count();
        });

        Mockito.when(boardRepository.countByIdNotAndCode(Mockito.anyLong(), Mockito.anyString())).thenAnswer(answer -> {
            final long id = answer.getArgument(0);
            final String code = answer.getArgument(1);
            return boards.stream().filter(item -> !item.getId().equals(id) && item.getCode().equals(code)).count();
        });

        Mockito.doAnswer(answer -> {
            final Long id = answer.getArgument(0);
            deleteBoard(id);
            return null;
        }).when(boardRepository).deleteById(Mockito.anyLong());

        Mockito.when(boardRepository.existsById(Mockito.anyLong())).thenAnswer(answer -> {
            final Long id = answer.getArgument(0);
            return boards.stream().anyMatch(item -> item.getId().equals(id));
        });

        Mockito.when(boardRepository.findAll(Mockito.any(BoardSearchCriteriaDo.class), Mockito.any(Pageable.class)))
                .thenAnswer(answer -> new PageImpl<>(boards, Pageable.unpaged(), boards.size()));

        Mockito.when(boardRepository.findById(Mockito.anyLong())).thenAnswer(answer -> {
            final Long id = answer.getArgument(0);
            return boards.stream().filter(item -> item.getId().equals(id)).findFirst();
        });

        Mockito.when(boardRepository.save(Mockito.any(BoardDo.class))).thenAnswer(answer -> {
            final BoardDo newItem = answer.getArgument(0);
            if (newItem.getId() == null) {
                newItem.setId(boardId.getAndIncrement());
            } else {
                final Optional<BoardDo> savedItem = boards.stream()
                        .filter(b -> b.getId().equals(newItem.getId()))
                        .findFirst();
                savedItem.ifPresent(boards::remove);
            }
            boards.add(newItem);
            return newItem;
        });
    }

    public void mock(final CodeListItemRepository codeListItemRepository) {
        Mockito.when(codeListItemRepository.countByCode(Mockito.anyString())).thenAnswer(answer -> {
            final String code = answer.getArgument(0);
            return codeListItems.stream().filter(item -> item.getCode().equals(code)).count();
        });

        Mockito.when(codeListItemRepository.countByCodeListIdAndParentIdNull(Mockito.anyLong())).thenAnswer(answer -> {
            final Long codeListId = answer.getArgument(0);
            return codeListItems.stream().filter(item -> !item.getCodeListId().equals(codeListId) && item.getParentId() == null).count();
        });

        Mockito.when(codeListItemRepository.countByIdNotAndCode(Mockito.anyLong(), Mockito.anyString())).thenAnswer(answer -> {
            final long id = answer.getArgument(0);
            final String code = answer.getArgument(1);
            return codeListItems.stream().filter(item -> !item.getId().equals(id) && item.getCode().equals(code)).count();
        });

        Mockito.when(codeListItemRepository.countByParentId(Mockito.anyLong())).thenAnswer(answer -> {
            final Long parentId = answer.getArgument(0);
            return codeListItems.stream().filter(item -> parentId.equals(item.getParentId())).count();
        });

        Mockito.doAnswer(answer -> {
            final Long id = answer.getArgument(0);
            deleteCodeListItem(id);
            return null;
        }).when(codeListItemRepository).deleteById(Mockito.anyLong());

        Mockito.when(codeListItemRepository.existsById(Mockito.anyLong())).thenAnswer(answer -> {
            final Long id = answer.getArgument(0);
            return codeListItems.stream().anyMatch(item -> item.getId().equals(id));
        });

        Mockito.when(codeListItemRepository.findAll(Mockito.any(CodeListItemSearchCriteriaDo.class), Mockito.any(Pageable.class)))
                .thenAnswer(answer -> new PageImpl<>(codeListItems, Pageable.unpaged(), codeListItems.size()));

        Mockito.when(codeListItemRepository.findById(Mockito.anyLong())).thenAnswer(answer -> {
            final Long id = answer.getArgument(0);
            return codeListItems.stream().filter(item -> item.getId().equals(id)).findFirst();
        });

        Mockito.when(codeListItemRepository.save(Mockito.any(CodeListItemDo.class))).thenAnswer(answer -> {
            final CodeListItemDo newItem = answer.getArgument(0);
            if (newItem.getId() == null) {
                newItem.setId(codeListItemId.getAndIncrement());
            } else {
                final Optional<CodeListItemDo> savedItem = codeListItems.stream()
                        .filter(item -> item.getId().equals(newItem.getId()))
                        .findFirst();
                savedItem.ifPresent(codeListItems::remove);
            }
            codeListItems.add(newItem);
            return newItem;
        });
    }

    public void mock(final CodeListRepository codeListRepository) {
        Mockito.when(codeListRepository.countByCode(Mockito.anyString())).thenAnswer(answer -> {
            final String code = answer.getArgument(0);
            return codeLists.stream().filter(item -> item.getCode().equals(code)).count();
        });

        Mockito.when(codeListRepository.countByIdNotAndCode(Mockito.anyLong(), Mockito.anyString())).thenAnswer(answer -> {
            final long id = answer.getArgument(0);
            final String code = answer.getArgument(1);
            return codeLists.stream().filter(item -> !item.getId().equals(id) && item.getCode().equals(code)).count();
        });

        Mockito.doAnswer(answer -> {
            final Long id = answer.getArgument(0);
            deleteCodeList(id);
            return null;
        }).when(codeListRepository).deleteById(Mockito.anyLong());

        Mockito.when(codeListRepository.existsById(Mockito.anyLong())).thenAnswer(answer -> {
            final Long id = answer.getArgument(0);
            return codeLists.stream().anyMatch(item -> item.getId().equals(id));
        });

        Mockito.when(codeListRepository.findAll(Mockito.any(CodeListSearchCriteriaDo.class), Mockito.any(Pageable.class)))
                .thenAnswer(answer -> new PageImpl<>(codeLists, Pageable.unpaged(), codeLists.size()));

        Mockito.when(codeListRepository.findById(Mockito.anyLong())).thenAnswer(answer -> {
            final Long id = answer.getArgument(0);
            return codeLists.stream().filter(item -> item.getId().equals(id)).findFirst();
        });

        Mockito.when(codeListRepository.save(Mockito.any(CodeListDo.class))).thenAnswer(answer -> {
            final CodeListDo newItem = answer.getArgument(0);
            if (newItem.getId() == null) {
                newItem.setId(codeListId.getAndIncrement());
            } else {
                final Optional<CodeListDo> savedItem = codeLists.stream()
                        .filter(item -> item.getId().equals(newItem.getId()))
                        .findFirst();
                savedItem.ifPresent(codeLists::remove);
            }
            codeLists.add(newItem);
            return newItem;
        });
    }

    public void mock(final EdgeCodeListItemRepository edgeCodeListItemRepository) {
        Mockito.when(edgeCodeListItemRepository.findByEdgeId(Mockito.anyLong()))
                .thenAnswer(answer -> {
                    final Long id = answer.getArgument(0);
                    final List<Long> codeListItemIds = edgeCodeListItems.getOrDefault(id, Collections.emptyList());
                    return codeListItemIds.stream()
                            .flatMap(codeListItemId -> codeListItems.stream().filter(item -> item.getId().equals(codeListItemId)))
                            .toList();
                });

        Mockito.doAnswer(answer -> {
            final Long id = answer.getArgument(0);
            final List<Long> codeListItemIds = answer.getArgument(1);
            edgeCodeListItems.put(id, codeListItemIds);
            return null;
        }).when(edgeCodeListItemRepository).saveAll(Mockito.anyLong(), Mockito.any());
    }

    public void mock(final EdgeImageRepository edgeImageRepository) {
        Mockito.doAnswer(answer -> {
            final Long id = answer.getArgument(0);
            edgeImages.remove(id);
            return null;
        }).when(edgeImageRepository).deleteByEdgeId(Mockito.anyLong());

        Mockito.when(edgeImageRepository.findByEdgeId(Mockito.anyLong()))
                .thenAnswer(answer -> {
                    final Long id = answer.getArgument(0);
                    return Optional.ofNullable(edgeImages.get(id));
                });

        Mockito.when(edgeImageRepository.save(Mockito.any(EdgeImageDo.class)))
                .thenAnswer(answer -> {
                    final EdgeImageDo newItem = answer.getArgument(0);
                    edgeImages.put(newItem.getEdgeId(), newItem);
                    return newItem;
                });
    }

    public void mock(final EdgeRepository edgeRepository) {
        Mockito.when(edgeRepository.countByCode(Mockito.anyString())).thenAnswer(answer -> {
            final String code = answer.getArgument(0);
            return edges.stream().filter(item -> item.getCode().equals(code)).count();
        });

        Mockito.when(edgeRepository.countByIdNotAndCode(Mockito.anyLong(), Mockito.anyString())).thenAnswer(answer -> {
            final long id = answer.getArgument(0);
            final String code = answer.getArgument(1);
            return edges.stream().filter(item -> !item.getId().equals(id) && item.getCode().equals(code)).count();
        });

        Mockito.doAnswer(answer -> {
            final Long id = answer.getArgument(0);
            deleteEdge(id);
            return null;
        }).when(edgeRepository).deleteById(Mockito.anyLong());

        Mockito.when(edgeRepository.existsById(Mockito.anyLong())).thenAnswer(answer -> {
            final Long id = answer.getArgument(0);
            return edges.stream().anyMatch(item -> item.getId().equals(id));
        });

        Mockito.when(edgeRepository.findAll(Mockito.any(EdgeSearchCriteriaDo.class), Mockito.any(Pageable.class)))
                .thenAnswer(answer -> new PageImpl<>(edges, Pageable.unpaged(), edges.size()));

        Mockito.when(edgeRepository.findById(Mockito.anyLong())).thenAnswer(answer -> {
            final Long id = answer.getArgument(0);
            return edges.stream().filter(item -> item.getId().equals(id)).findFirst();
        });

        Mockito.when(edgeRepository.save(Mockito.any(EdgeDo.class))).thenAnswer(answer -> {
            final EdgeDo newItem = answer.getArgument(0);
            if (newItem.getId() == null) {
                newItem.setId(edgeId.getAndIncrement());
            } else {
                final Optional<EdgeDo> savedItem = edges.stream()
                        .filter(item -> item.getId().equals(newItem.getId()))
                        .findFirst();
                savedItem.ifPresent(edges::remove);
            }
            edges.add(newItem);
            return newItem;
        });
    }

    public void mock(final OrderCommentRepository orderCommentRepository) {
        Mockito.when(orderCommentRepository.findAllByOrderId(Mockito.anyLong())).thenAnswer(answer -> {
            final Long id = answer.getArgument(0);
            return orderComments.stream().filter(item -> item.getOrderId().equals(id)).toList();
        });

        Mockito.when(orderCommentRepository.save(Mockito.any(OrderCommentDo.class))).thenAnswer(answer -> {
            final OrderCommentDo newItem = answer.getArgument(0);
            if (newItem.getId() == null) {
                newItem.setId(orderCommentId.getAndIncrement());
            } else {
                final Optional<OrderCommentDo> savedItem = orderComments.stream()
                        .filter(item -> item.getId().equals(newItem.getId()))
                        .findFirst();
                savedItem.ifPresent(orderComments::remove);
            }
            orderComments.add(newItem);
            return newItem;
        });
    }

    public void mock(final OrderItemRepository orderItemRepository) {
        Mockito.when(orderItemRepository.countByOrderId(Mockito.anyLong())).thenAnswer(answer -> {
            final Long id = answer.getArgument(0);
            return orderItems.getOrDefault(id, Collections.emptyList()).size();
        });

        Mockito.doAnswer(answer -> {
            final Long id = answer.getArgument(0);
            deleteOrderItem(id);
            return null;
        }).when(orderItemRepository).deleteById(Mockito.anyLong());

        Mockito.when(orderItemRepository.findById(Mockito.anyLong())).thenAnswer(answer -> {
            final Long id = answer.getArgument(0);
            return orderItems.values().stream().flatMap(Collection::stream)
                    .filter(item -> item.getId().equals(id)).findFirst();
        });

        Mockito.when(orderItemRepository.findByOrderIdAndSortNum(Mockito.anyLong(), Mockito.anyInt())).thenAnswer(answer -> {
            final Long id = answer.getArgument(0);
            final Integer sortNum = answer.getArgument(1);
            return orderItems.getOrDefault(id, Collections.emptyList()).stream()
                    .filter(item -> item.getSortNum().equals(sortNum)).findFirst();
        });

        Mockito.when(orderItemRepository.findAllByOrderId(Mockito.anyLong())).thenAnswer(answer -> {
            final Long id = answer.getArgument(0);
            return orderItems.getOrDefault(id, Collections.emptyList())
                    .stream().sorted(Comparator.comparing(OrderItemDo::getSortNum))
                    .toList();
        });

        Mockito.when(orderItemRepository.insert(Mockito.any(OrderItemDo.class))).thenAnswer(answer -> {
            final OrderItemDo newItem = answer.getArgument(0);
            newItem.setId(orderItemId.getAndIncrement());
            final List<OrderItemDo> items = orderItems.getOrDefault(newItem.getOrderId(), new LinkedList<>());
            items.add(newItem);
            orderItems.put(newItem.getOrderId(), items);
            return newItem;
        });

        Mockito.doAnswer(answer -> {
            final Long id = answer.getArgument(0);
            final Integer sortNum = answer.getArgument(1);
            orderItems.values().stream().flatMap(Collection::stream)
                    .filter(item -> item.getId().equals(id))
                    .forEach(item -> {
                        item.setSortNum(sortNum);
                    });
            return null;
        }).when(orderItemRepository).setSortNum(Mockito.anyLong(), Mockito.anyInt());

        Mockito.doAnswer(answer -> {
            final Long id = answer.getArgument(0);
            final String name = answer.getArgument(1);
            orderItems.values().stream().flatMap(Collection::stream)
                    .filter(item -> item.getId().equals(id))
                    .forEach(item -> {
                        item.setName(name);
                    });
            return null;
        }).when(orderItemRepository).setName(Mockito.anyLong(), Mockito.anyString());

        Mockito.doAnswer(answer -> {
            final Long id = answer.getArgument(0);
            final String description = answer.getArgument(1);
            orderItems.values().stream().flatMap(Collection::stream)
                    .filter(item -> item.getId().equals(id))
                    .forEach(item -> {
                        item.setDescription(description);
                    });
            return null;
        }).when(orderItemRepository).setDescription(Mockito.anyLong(), Mockito.anyString());

        Mockito.doAnswer(answer -> {
            final Long id = answer.getArgument(0);
            final Integer quantity = answer.getArgument(1);
            orderItems.values().stream().flatMap(Collection::stream)
                    .filter(item -> item.getId().equals(id))
                    .forEach(item -> {
                        item.setQuantity(quantity);
                    });
            return null;
        }).when(orderItemRepository).setQuantity(Mockito.anyLong(), Mockito.anyInt());

        Mockito.doAnswer(answer -> {
            final Long id = answer.getArgument(0);
            final String itemPart = answer.getArgument(1);
            orderItems.values().stream().flatMap(Collection::stream)
                    .filter(item -> item.getId().equals(id))
                    .forEach(item -> {
                        item.setPart(itemPart);
                    });
            return null;
        }).when(orderItemRepository).setPart(Mockito.anyLong(), Mockito.anyString());
    }

    public void mock(final OrderItemSummaryRepository orderItemSummaryRepository) {
        Mockito.doAnswer(answer -> {
            final Long id = answer.getArgument(0);
            final List<OrderItemSummaryDo> savedItems = orderItemSummaries.stream()
                    .filter(item -> item.getOrderItemId().equals(id))
                    .toList();
            orderItemSummaries.removeAll(savedItems);
            return null;
        }).when(orderItemSummaryRepository).deleteByOrderItemId(Mockito.anyLong());

        Mockito.when(orderItemSummaryRepository.findAllByOrderItemId(Mockito.anyLong()))
                .thenAnswer(answer -> {
                    final Long id = answer.getArgument(0);
                    return orderItemSummaries.stream().filter(item -> item.getOrderItemId().equals(id)).toList();
                });

        Mockito.when(orderItemSummaryRepository.insert(Mockito.any(OrderItemSummaryDo.class))).thenAnswer(answer -> {
            final OrderItemSummaryDo newItem = answer.getArgument(0);
            orderItemSummaries.add(newItem);
            return newItem;
        });
    }

    public void mock(final OrderMaterialRepository orderMaterialRepository) {
        Mockito.when(orderMaterialRepository.countById(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString()))
                .thenAnswer(answer -> {
                    final Long orderId = answer.getArgument(0);
                    final Long materialId = answer.getArgument(1);
                    final String code = answer.getArgument(2);
                    return orderMaterials.stream().filter(item -> isMaterialId(orderId, materialId, code, item)).count();
                });

        Mockito.doAnswer(answer -> {
            final Long orderId = answer.getArgument(0);
            final Long materialId = answer.getArgument(1);
            final String code = answer.getArgument(2);
            final Optional<OrderMaterialDo> savedItem = orderMaterials.stream()
                    .filter(item -> isMaterialId(orderId, materialId, code, item))
                    .findFirst();
            savedItem.ifPresent(orderMaterials::remove);
            return null;
        }).when(orderMaterialRepository).deleteById(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString());

        Mockito.when(orderMaterialRepository.findById(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString()))
                .thenAnswer(answer -> {
                    final Long orderId = answer.getArgument(0);
                    final Long materialId = answer.getArgument(1);
                    final String code = answer.getArgument(2);
                    return orderMaterials.stream()
                            .filter(item -> isMaterialId(orderId, materialId, code, item))
                            .findFirst();
                });

        Mockito.when(orderMaterialRepository.findAllByOrderId(Mockito.anyLong())).thenAnswer(answer -> {
            final Long id = answer.getArgument(0);
            return orderMaterials.stream().filter(item -> item.getOrderId().equals(id))
                    .toList();
        });

        Mockito.when(orderMaterialRepository.save(Mockito.any(OrderMaterialDo.class))).thenAnswer(answer -> {
            final OrderMaterialDo newItem = answer.getArgument(0);
            final Optional<OrderMaterialDo> savedItem = orderMaterials.stream()
                    .filter(item -> isMaterialId(newItem.getOrderId(), newItem.getMaterialId(), newItem.getCode(), item))
                    .findFirst();
            savedItem.ifPresent(orderMaterials::remove);
            orderMaterials.add(newItem);
            return newItem;
        });
    }

    public void mock(final OrderNumberRepository orderNumberRepository) {
        Mockito.when(orderNumberRepository.getNextOrderNumber(Mockito.anyLong())).thenAnswer(answer -> {
            final Long id = answer.getArgument(0);
            final Long number = orderNumbers.getOrDefault(id, 0L) + 1L;
            orderNumbers.put(id, number);
            return number;
        });
    }

    public void mock(final OrderRepository orderRepository) {
        Mockito.doAnswer(answer -> {
            final Long id = answer.getArgument(0);
            deleteOrder(id);
            return null;
        }).when(orderRepository).deleteById(Mockito.anyLong());

        Mockito.when(orderRepository.getOrderUserId(Mockito.anyLong())).thenAnswer(answer -> {
            final Long id = answer.getArgument(0);
            return orders.stream().filter(item -> item.getId().equals(id)).map(OrderDo::getUserId).findFirst();
        });

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenAnswer(answer -> {
            final Long id = answer.getArgument(0);
            return orders.stream().filter(item -> item.getId().equals(id)).findFirst();
        });

        Mockito.when(orderRepository.insert(Mockito.any(OrderDo.class))).thenAnswer(answer -> {
            final OrderDo newItem = answer.getArgument(0);
            newItem.setId(orderId.getAndIncrement());
            orders.add(newItem);
            return newItem;
        });

        Mockito.doAnswer(answer -> {
            final Long id = answer.getArgument(0);
            final String contact = answer.getArgument(1);
            orders.stream().filter(item -> item.getId().equals(id))
                    .forEach(item -> item.setContact(contact));
            return null;
        }).when(orderRepository).setContact(Mockito.anyLong(), Mockito.anyString());

        Mockito.doAnswer(answer -> {
            final Long id = answer.getArgument(0);
            final LocalDate delivery = answer.getArgument(1);
            orders.stream().filter(item -> item.getId().equals(id))
                    .forEach(item -> item.setDelivery(delivery));
            return null;
        }).when(orderRepository).setDelivery(Mockito.anyLong(), Mockito.any(LocalDate.class));

        Mockito.doAnswer(answer -> {
            final Long id = answer.getArgument(0);
            final OrderPackageType packageType = answer.getArgument(1);
            orders.stream().filter(item -> item.getId().equals(id))
                    .forEach(item -> item.setPackageType(packageType));
            return null;
        }).when(orderRepository).setPackageType(Mockito.anyLong(), Mockito.any(OrderPackageType.class));

        Mockito.doAnswer(answer -> {
            final Long id = answer.getArgument(0);
            final BigDecimal weight = answer.getArgument(1);
            orders.stream().filter(item -> item.getId().equals(id))
                    .forEach(item -> item.setWeight(weight));
            return null;
        }).when(orderRepository).setWeight(Mockito.anyLong(), Mockito.any(BigDecimal.class));

        Mockito.doAnswer(answer -> {
            final Long id = answer.getArgument(0);
            final BigDecimal total = answer.getArgument(1);
            orders.stream().filter(item -> item.getId().equals(id))
                    .forEach(item -> item.setTotal(total));
            return null;
        }).when(orderRepository).setTotal(Mockito.anyLong(), Mockito.any(BigDecimal.class));

        Mockito.doAnswer(answer -> {
            final Long id = answer.getArgument(0);
            final String summary = answer.getArgument(1);
            orders.stream().filter(item -> item.getId().equals(id))
                    .forEach(item -> item.setSummary(summary));
            return null;
        }).when(orderRepository).setSummary(Mockito.anyLong(), Mockito.anyString());
    }

    public void mock(final OrderStatusRepository orderStatusRepository) {
        Mockito.when(orderStatusRepository.findAllByOrderId(Mockito.anyLong()))
                .thenAnswer(answer -> {
                    final Long id = answer.getArgument(0);
                    return orderStatuses.getOrDefault(id, Collections.emptyList());
                });

        Mockito.when(orderStatusRepository.save(Mockito.any(OrderStatusDo.class))).thenAnswer(answer -> {
            final OrderStatusDo newItem = answer.getArgument(0);
            final List<OrderStatusDo> items = orderStatuses.getOrDefault(newItem.getOrderId(), new LinkedList<>());
            if (newItem.getId() == null) {
                newItem.setId(orderStatusId.getAndIncrement());
            } else {
                final Optional<OrderStatusDo> savedItem = items.stream()
                        .filter(item -> item.getId().equals(newItem.getId()))
                        .findFirst();
                savedItem.ifPresent(items::remove);
            }
            items.add(newItem);
            orderStatuses.put(newItem.getOrderId(), items);
            return newItem;
        });
    }

    public void mock(final OrderSummaryViewRepository orderSummaryViewRepository) {
        Mockito.when(orderSummaryViewRepository.findAllById(Mockito.anyLong())).thenAnswer(answer -> {
            final Long id = answer.getArgument(0);
            final List<Long> itemIds = orderItems.getOrDefault(id, Collections.emptyList()).stream().map(OrderItemDo::getId).toList();
            return orderItemSummaries.stream()
                    .filter(item -> itemIds.contains(item.getOrderItemId()) && item.getCode().startsWith("TOTAL:"))
                    .collect(Collectors.groupingBy(OrderItemSummaryDo::getCode)).values().stream()
                    .map(items -> new OrderSummaryViewDo(
                            id,
                            items.stream().findFirst().map(OrderItemSummaryDo::getCode).orElse(""),
                            items.stream().map(OrderItemSummaryDo::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add)
                    ))
                    .toList();
        });
    }

    public void mock(final OrderViewRepository orderViewRepository) {
        Mockito.when(orderViewRepository.findById(Mockito.anyLong())).thenAnswer(answer -> {
            final Long id = answer.getArgument(0);
            return orders.stream().filter(item -> item.getId().equals(id))
                    .map(item -> mapOrderView(item, id))
                    .findFirst();
        });

        Mockito.when(orderViewRepository.findAll(Mockito.any(OrderViewSearchCriteriaDo.class), Mockito.any(Pageable.class)))
                .thenAnswer(answer -> new PageImpl<>(orders.stream().map(item -> mapOrderView(item, item.getId())).toList(), Pageable.unpaged(), orders.size()));
    }

    public void mock(final UserRepository userRepository) {
        Mockito.when(userRepository.count()).thenReturn(users.size());

        Mockito.doAnswer(answer -> {
            final Long id = answer.getArgument(0);
            deleteUser(id);
            return null;
        }).when(userRepository).deleteById(Mockito.anyLong());

        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenAnswer(answer -> {
            final String email = answer.getArgument(0);
            return users.stream().anyMatch(item -> item.getEmail().equals(email));
        });

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenAnswer(answer -> {
            final Long id = answer.getArgument(0);
            return users.stream().anyMatch(item -> item.getId().equals(id));
        });

        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenAnswer(answer -> {
            final String username = answer.getArgument(0);
            return users.stream().anyMatch(item -> item.getUsername().equals(username));
        });

        Mockito.when(userRepository.findAll(Mockito.any(UserSearchCriteriaDo.class), Mockito.any(Pageable.class)))
                .thenAnswer(answer -> new PageImpl<>(users, Pageable.unpaged(), users.size()));

        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenAnswer(answer -> {
            final String email = answer.getArgument(0);
            return users.stream().filter(item -> item.getEmail().equals(email)).findFirst();
        });

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenAnswer(answer -> {
            final Long id = answer.getArgument(0);
            return users.stream().filter(item -> item.getId().equals(id)).findFirst();
        });

        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenAnswer(answer -> {
            final String username = answer.getArgument(0);
            return users.stream().filter(item -> item.getUsername().equals(username)).findFirst();
        });

        Mockito.when(userRepository.save(Mockito.any(UserDo.class))).thenAnswer(answer -> {
            final UserDo newItem = answer.getArgument(0);
            if (newItem.getId() == null) {
                newItem.setId(userId.getAndIncrement());
            } else {
                final Optional<UserDo> savedItem = users.stream()
                        .filter(item -> item.getId().equals(newItem.getId()))
                        .findFirst();
                savedItem.ifPresent(users::remove);
            }
            users.add(newItem);
            return newItem;
        });
    }

    private void deleteBoard(final Long id) {
        final Optional<BoardDo> savedItem = boards.stream().filter(item -> item.getId().equals(id)).findFirst();
        savedItem.ifPresent(boards::remove);

        boardImages.remove(id);

        boardCodeListItems.remove(id);
    }

    private void deleteCodeListItem(final Long id) {
        final Optional<CodeListItemDo> savedItem = codeListItems.stream().filter(item -> item.getId().equals(id)).findFirst();
        savedItem.ifPresent(codeListItems::remove);

        boardCodeListItems.values().forEach(items -> items.remove(id));

        edgeCodeListItems.values().forEach(items -> items.remove(id));
    }

    private void deleteCodeList(final Long id) {
        final Optional<CodeListDo> savedItem = codeLists.stream().filter(item -> item.getId().equals(id)).findFirst();
        savedItem.ifPresent(codeLists::remove);

        final List<Long> itemIds = codeListItems.stream()
                .filter(item -> item.getCodeListId().equals(id))
                .map(CodeListItemDo::getId)
                .toList();
        itemIds.forEach(this::deleteCodeListItem);
    }

    private void deleteEdge(final Long id) {
        final Optional<EdgeDo> savedItem = edges.stream().filter(item -> item.getId().equals(id)).findFirst();
        savedItem.ifPresent(edges::remove);

        edgeImages.remove(id);

        edgeCodeListItems.remove(id);
    }

    private void deleteOrderItem(final Long id) {
        final Optional<OrderItemDo> savedItem = orderItems.values().stream().flatMap(Collection::stream)
                .filter(item -> item.getId().equals(id)).findFirst();
        savedItem.ifPresent(item -> orderItems.getOrDefault(item.getOrderId(), Collections.emptyList()).remove(item));

        final List<OrderItemSummaryDo> summaries = orderItemSummaries.stream()
                .filter(item -> item.getOrderItemId().equals(id)).toList();
        orderItemSummaries.removeAll(summaries);
    }

    private void deleteOrder(final Long id) {
        final Optional<OrderDo> savedItem = orders.stream().filter(item -> item.getId().equals(id)).findFirst();
        savedItem.ifPresent(orders::remove);

        final List<OrderCommentDo> comments = orderComments.stream().filter(item -> item.getOrderId().equals(id)).toList();
        orderComments.removeAll(comments);

        orderItems.getOrDefault(id, Collections.emptyList()).stream().map(OrderItemDo::getId).forEach(this::deleteOrderItem);

        final List<OrderMaterialDo> materials = orderMaterials.stream().filter(item -> item.getOrderId().equals(id)).toList();
        orderMaterials.removeAll(materials);

        orderStatuses.remove(id);
    }

    private void deleteUser(final Long id) {
        final Optional<UserDo> savedItem = users.stream().filter(item -> item.getId().equals(id)).findFirst();
        savedItem.ifPresent(users::remove);

        userAuthorities.remove(id);
    }

    private boolean isMaterialId(final long orderId, final long materialId, final String code, final OrderMaterialDo item) {
        return item.getOrderId().equals(orderId)
                && item.getMaterialId().equals(materialId)
                && item.getCode().equals(code);
    }

    private OrderViewDo mapOrderView(final OrderDo item, final Long id) {
        return new OrderViewDo(
                id,
                item.getUserId(),
                item.getCreated(),
                item.getOrderNumber(),
                item.getContact(),
                item.getDelivery(),
                item.getPackageType(),
                orderStatuses.getOrDefault(id, Collections.emptyList()).stream()
                        .map(OrderStatusDo::getStatus)
                        .reduce((first, second) -> second)
                        .orElse(OrderStatus.NEW),
                item.getWeight(),
                item.getTotal()
        );
    }
}
