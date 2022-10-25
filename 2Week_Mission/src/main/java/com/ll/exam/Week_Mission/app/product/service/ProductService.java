package com.ll.exam.Week_Mission.app.product.service;

import com.ll.exam.Week_Mission.app.exception.DataNotFoundException;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.post.entity.Post;
import com.ll.exam.Week_Mission.app.post.domain.hashtag.entity.PostHashTag;
import com.ll.exam.Week_Mission.app.post.domain.hashtag.service.PostHashTagService;
import com.ll.exam.Week_Mission.app.post.domain.keyword.entity.PostKeyword;
import com.ll.exam.Week_Mission.app.post.domain.keyword.service.PostKeywordService;
import com.ll.exam.Week_Mission.app.product.domain.tag.entity.ProductTag;
import com.ll.exam.Week_Mission.app.product.domain.tag.service.ProductTagService;
import com.ll.exam.Week_Mission.app.product.entity.Product;
import com.ll.exam.Week_Mission.app.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final PostKeywordService postKeywordService;
    private final ProductTagService productTagService;
    private final PostHashTagService postTagService;


    public List<Product> findAllForPrintByOrderByIdDesc(Member actor) {
        List<Product> products = findAllByOrderByIdDesc();

        loadForPrintData(products, actor);

        return products;
    }

    public List<Product> findAllByOrderByIdDesc() {
        return productRepository.findAllByOrderByIdDesc();
    }

    private void loadForPrintData(List<Product> products, Member actor) {
        long[] ids = products
                .stream()
                .mapToLong(Product::getId)
                .toArray();

        List<ProductTag> productTagsByProductIds = productTagService.getProductTagsByProductIdIn(ids);

        Map<Long, List<ProductTag>> productTagsByProductIdMap = productTagsByProductIds.stream()
                .collect(groupingBy(
                        productTag -> productTag.getProduct().getId(), toList()
                ));

        products.stream().forEach(product -> {
            List<ProductTag> productTags = productTagsByProductIdMap.get(product.getId());

            if (productTags == null || productTags.size() == 0) return;

            product.getExtra().put("productTags", productTags);
        });
    }

    public Optional<Product> findForPrintById(long id) {
        Optional<Product> opProduct = findById(id);

        if (opProduct.isEmpty()) return opProduct;

        List<ProductTag> productTags = getProductTags(opProduct.get());

        opProduct.get().getExtra().put("productTags", productTags);

        return opProduct;
    }

    public Optional<Product> findById(long id) {
        return productRepository.findById(id);
    }

    private List<ProductTag> getProductTags(Product product) {
        return productTagService.getProductTags(product);
    }

    public List<Post> findPostsByProduct(Product product) {
        Member author = product.getAuthor();
        PostKeyword postKeyword = product.getPostKeyword();
        List<PostHashTag> postTags = postTagService.getPostTags(author.getId(), postKeyword.getId());

        // 게시글 태그 리스트에서 게시글 리스트만 추출하여 리턴
        return postTags
                .stream()
                .map(PostHashTag::getPost)
                .collect(Collectors.toList());
    }

    @Transactional
    public Product create(Member author, String subject, int price, long postKeywordId, String productTagContents) {
        PostKeyword postKeyword = postKeywordService.findById(postKeywordId).orElseThrow(()-> new DataNotFoundException("해당 글 태그가 존재하지 않습니다"));

        return create(author, subject, price, postKeyword, productTagContents);
    }

    @Transactional
    public Product create(Member author, String subject, int price, PostKeyword postKeyword, String productTagContents) {
        Product product = Product
                .builder()
                .author(author)
                .postKeyword(postKeyword)
                .subject(subject)
                .price(price)
                .build();

        productRepository.save(product);

        applyProductTags(product, productTagContents);

        return product;
    }

    @Transactional
    public void applyProductTags(Product product, String productTagContents) {
        productTagService.applyProductTags(product, productTagContents);
    }

    @Transactional
    public void modify(Product product, String subject, int price, String productTagContents) {
        product.setSubject(subject);
        product.setPrice(price);

        applyProductTags(product, productTagContents);
    }

    @Transactional
    public void remove(Product product) {
        productRepository.delete(product);
    }

    public boolean actorCanModify(Member actor, Product product) {
        if (actor == null) return false;

        return actor.getId().equals(product.getAuthor().getId());
    }

    public boolean actorCanRemove(Member actor, Product product) {
        return actorCanModify(actor, product);
    }

    public List<ProductTag> getProductTags(String productTagContent, Member actor) {
        List<ProductTag> productTags = productTagService.getProductTags(productTagContent);

        loadForPrintDataOnProductTagList(productTags, actor);

        return productTags;
    }

    private void loadForPrintDataOnProductTagList(List<ProductTag> productTags, Member actor) {
        List<Product> products = productTags
                .stream()
                .map(ProductTag::getProduct)
                .collect(toList());

        loadForPrintData(products, actor);
    }
}
