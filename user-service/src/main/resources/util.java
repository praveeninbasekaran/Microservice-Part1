import java.util.*;
import java.util.stream.Collectors;

public class RoleListReorderUtil {

    public static List<IDPProductResponseDTO> reorderRolesByDefault(String defaultRole, List<IDPProductResponseDTO> roles) {
        if (defaultRole == null || roles == null || roles.isEmpty()) {
            return roles;
        }

        // 1. Group by product preserving insertion order
        LinkedHashMap<String, List<IDPProductResponseDTO>> groupedByProduct = new LinkedHashMap<>();
        for (IDPProductResponseDTO role : roles) {
            groupedByProduct.computeIfAbsent(role.getProduct(), k -> new ArrayList<>()).add(role);
        }

        // 2. Find the product group where default role resides
        String defaultRoleProduct = null;
        for (IDPProductResponseDTO role : roles) {
            if (role.getRole().equals(defaultRole)) {
                defaultRoleProduct = role.getProduct();
                break;
            }
        }

        // 3. Reorder default role to first inside its product group
        if (defaultRoleProduct != null) {
            List<IDPProductResponseDTO> productRoles = groupedByProduct.get(defaultRoleProduct);
            if (productRoles != null) {
                Optional<IDPProductResponseDTO> defaultRoleObjOpt = productRoles.stream()
                    .filter(r -> r.getRole().equals(defaultRole))
                    .findFirst();

                if (defaultRoleObjOpt.isPresent()) {
                    IDPProductResponseDTO defaultRoleObj = defaultRoleObjOpt.get();
                    productRoles.remove(defaultRoleObj);
                    productRoles.add(0, defaultRoleObj);
                }
            }
        }

List<IDPProductResponseDTO> reorderedList = RoleListReorderUtil.reorderRolesByDefault(defaultRole, idpProductList);
roleSwitchResponse.setRoles(reorderedList);

        // 4. Flatten groups back into a single list preserving product order
        return groupedByProduct.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}

