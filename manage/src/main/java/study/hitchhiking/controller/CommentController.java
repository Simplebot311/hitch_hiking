package study.hitchhiking.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import study.hitchhiking.VO.commentVO;
import study.hitchhiking.pojo.Comment;
import study.hitchhiking.service.CommentService;
import study.hitchhiking.service.OrdersService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 吴建豪
 * @since 2023-03-24
 */
@Controller
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private CommentService commentService;

    @RequestMapping("/select")
    public String getComment(@RequestParam(name = "target", required = false) String target,
                            @RequestParam(name = "typeOfSelect", required = false) String typeOfSelect,
                            @RequestParam(name = "role", required = false) String[] role, Model model) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        //判断是否有查询条件
        if (null != role && role.length == 1) {
            wrapper.like("role", role[0]);
        }
        if (null != target && null != typeOfSelect) {
            wrapper.like(typeOfSelect, target);
        }
        //没有查询条件则查找全部，有条件则按照条件查询
        List<Comment> commentList = commentService.list(wrapper);
        model.addAttribute("commentList", commentVOList(commentList));
        return "commentSelect";
    }

    @RequestMapping("/insert")
    public String insertComment(@RequestParam(name = "orderID") String orderID,
                              @RequestParam(name = "userID") String userID,
                              @RequestParam(name = "role") String role,
                              @RequestParam(name = "content") String content,
                              @DateTimeFormat(fallbackPatterns = {"yyyy-MM-dd'T'HH:mm"})
                              @RequestParam(name = "commenttime") Date commenttime,
                              Model model) {
        Comment comment = new Comment();
        comment.setOrderID(Long.valueOf(orderID));
        comment.setUserID(Long.valueOf(userID));
        comment.setRole(role);
        comment.setContent(content);
        comment.setCommenttime(commenttime);
        commentService.save(comment);

        model.addAttribute("commentList", commentVOList(commentService.list(null)));
//        addUserVOList("userList", userService.list(null), model);
        return "commentSelect";
    }

    @RequestMapping("/delete")
    public String deleteComment(@RequestParam(name = "commentID") String commentID, Model model) {
        commentService.removeById(Long.valueOf(commentID));
        model.addAttribute("commentList",commentVOList(commentService.list(null)));
        return "commentSelect";
    }

    @RequestMapping("/details")
    public String getDetails(@RequestParam(name = "commentID") String commentID, Model model) {
        model.addAttribute("one",new commentVO(commentService.getById(commentID)));
        return "comment";
    }

    @RequestMapping("/update")
    public String updateComment(@RequestParam(name = "commentID") String commentID,
                              @RequestParam(name = "orderID") String orderID,
                              @RequestParam(name = "userID") String userID,
                              @RequestParam(name = "role") String role,
                              @RequestParam(name = "content") String content,
                              @DateTimeFormat(fallbackPatterns = {"yyyy-MM-dd'T'HH:mm"})
                              @RequestParam(name = "commenttime",required = false) Date commenttime,
                              Model model) {
        Comment comment = commentService.getById(commentID);
        if(!"不修改".equals(role)){
            comment.setRole(role);
        }
        comment.setUserID(Long.valueOf(userID));
        comment.setOrderID(Long.valueOf(orderID));
        comment.setContent(content);
        if(null != commenttime){
            comment.setCommenttime(commenttime);
        }
        commentService.updateById(comment);

        model.addAttribute("commentList", commentVOList(commentService.list(null)));
        //addUserVOList("userList", userService.list(null), model);
        return "commentSelect";
    }

    private List<commentVO> commentVOList(List<Comment> list){
        if(null == list || list.size()==0)return new ArrayList<>();
        List<commentVO> voList = new ArrayList<>();
        for (Comment comment : list) {
            voList.add(new commentVO(comment));
        }
        return voList;
    }
}

